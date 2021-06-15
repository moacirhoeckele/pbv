using Oracle.ManagedDataAccess.Client;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace CsvGenerator
{
    public partial class Form1 : Form
    {
        private readonly string _startupPath = Environment.CurrentDirectory;

        private static DataSet _dataSet;

        private static List<string[]> _parametersList;

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            _dataSet = new DataSet();
            _dataSet.ReadXml(_startupPath + @"\Data.xml");
            dgOperators.DataSource = _dataSet.Tables[0];

            dtpProductionDate.Value = DateTime.Today;
            txtFilePath.Text = @"C:\Temp\KITA4.csv";
            txtLog.Text = string.Empty;
        }

        private void DgOperators_RowEnter(object sender, DataGridViewCellEventArgs e)
        {
            if (dgOperators.SelectedRows.Count == 0) return;

            var selectedOperator = dgOperators.SelectedRows[0];
            var operatorId = selectedOperator.Cells["ID"].Value.ToString();
            var dvKits = new DataView(_dataSet.Tables[1], "Operator = '" + operatorId + "'", null, DataViewRowState.CurrentRows);
            dgKits.DataSource = dvKits;
        }

        private void BtnFind_Click(object sender, EventArgs e)
        {
            var result = fbdFilePath.ShowDialog();
            if (result == DialogResult.OK)
            {
                txtFilePath.Text = fbdFilePath.SelectedPath + @"\KITA4.csv";
            }
        }

        private void BtnCreate_Click(object sender, EventArgs e)
        {
            if (!CreatePickingRecords()) return;

            if (CreateFile())
            {
                MessageBox.Show("Arquivo gerado com sucesso!", "Sucesso", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }

        private bool CreateFile()
        {
            try
            {
                var queries = new List<string>();

                foreach (var param in _parametersList)
                {
                    var query = string.Format(_dataSet.Tables[2].Rows[1].ItemArray[0].ToString(),
                        param[3], //nu_kit
                        param[2], //production date
                        param[0], //station
                        param[1], //module
                        string.IsNullOrEmpty(param[4]) ? "null" : param[4] //initial sequence
                    );
                    queries.Add(query);
                }

                var finalQuery = string.Format(_dataSet.Tables[2].Rows[0].ItemArray[0].ToString(), string.Join(" union all ", queries));

                txtLog.Text = finalQuery;

                var pickingList = new List<Picking>();

                using (var conn = new OracleConnection(ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString))
                {
                    conn.Open();
                    var oda = new OracleDataAdapter(finalQuery, conn);
                    var dt = new DataTable();
                    oda.Fill(dt);

                    if (dt.Rows.Count == 0)
                    {
                        MessageBox.Show("Nenhum registro encontrado!", "Atenção", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);

                        return false;
                    }

                    foreach (DataRow dr in dt.Rows)
                    {
                        var picking = new Picking(
                            dr["dt_producao"].ToString(),
                            Convert.ToInt32(dr["cd_estacao"].ToString()),
                            Convert.ToInt32(dr["nu_modulo"].ToString()),
                            Convert.ToInt32(dr["seq_chassi"].ToString()),
                            Convert.ToInt32(dr["chassi"].ToString()),
                            Convert.ToInt32(dr["nu_box"].ToString()),
                            Convert.ToInt32(dr["cd_produto"].ToString()),
                            Convert.ToInt32(dr["qt_transmitida"].ToString()),
                            PickingStatus.Pendente);
                        pickingList.Add(picking);
                    }
                }

                /* Encontra a menor e a maior sequencia entre a lista de pickings
                   para utilizar na montagem de uma lista completa. */
                pickingList = pickingList.OrderBy(x => x.sequencia).ToList();
                var minSequence = pickingList.First().sequencia;
                var maxSequence = pickingList.Last().sequencia;

                /* Cria uma lista agrupada de kits. */
                var kits = pickingList.GroupBy(p => new {p.estacao, p.modulo}).Select(g => $"{g.Key.estacao}{g.Key.modulo}").ToList();

                // Nova lista para receber os objetos que serão exibidos na tela.
                var pickingViewModelList = new List<PickingViewModel>();

                /* Percorre cada kit */
                foreach (var kit in kits)
                {
                    // Encontra estação e módulo (Kit)
                    var station = int.Parse(kit.Substring(0, 3));
                    var module = int.Parse(kit.Substring(3));

                    // Nova lista para separar apenas os registros do Kit atual.
                    var kitList = pickingList.Where(p => p.estacao == station && p.modulo == module).ToList();

                    // Cria uma lista completa de sequencias desde a menor até a maior.
                    var diffSeq = new List<int>();
                    for (var i = minSequence; i <= maxSequence; i++)
                    {
                        diffSeq.Add(i);
                    }

                    // Cria uma lista apenas com as sequencias do kit atual.
                    var kitSeq = kitList.GroupBy(k => k.sequencia).Select(g => g.Key).ToList();

                    // Remove as sequencias do kit atual da lista de sequencias completa para obter a diferença.
                    diffSeq = diffSeq.Except(kitSeq).ToList();

                    /* Cria uma lista das peças agrupadas por box e part number para utilizar na montagem
                       da visualização. Formata no padrão 99999. */
                    var parts = kitList.GroupBy(x => new {x.produto, x.box}).Select(g => g.Key.produto + $"{g.Key.box:00}").ToList();

                    /* Verifica se há alguma sequencia na lista de diferenças e adiciona um registro falso se necessário
                       para criar uma lista completa sem furos de sequencia. */
                    var completeList = new List<Picking>();
                    completeList.AddRange(kitList);
                    foreach (var missing in diffSeq)
                    {
                        // Deve adicionar um registro para cada peça.
                        foreach (var partAux in parts)
                        {
                            // Encontra o part number e o box
                            var part = int.Parse(partAux.Substring(0, partAux.Length - 2));
                            var box = int.Parse(partAux.Substring(partAux.Length - 2));

                            completeList.Add(new Picking(
                                kitList.First().dataProducao,
                                kitList.First().estacao,
                                kitList.First().modulo,
                                missing,
                                missing, // utiliza a sequencia como chassi, não pode repetir.
                                box,
                                part,
                                0, // não tem quantidade
                                kitList.First().status));
                        }
                    }

                    // Ordena a lista completa pela sequencia.
                    completeList = completeList.OrderBy(x => x.sequencia).ToList();

                    /* Cria uma lista agrupada dos chassis para usar na divisão. */
                    var chassis = completeList.GroupBy(x => x.chassi).Select(g => g.Key).ToList();

                    /* Divide os chassis em grupos de 3. */
                    var chassiGroup = chassis.Select((x, i) => new {Index = i, Value = x})
                                             .GroupBy(x => x.Index / 3)
                                             .Select(x => x.Select(v => v.Value).ToList())
                                             .ToList();

                    // Percorre cada conjunto de 3 chassis
                    foreach (var group in chassiGroup)
                    {
                        // Percorre cada linha da lista agrupada de peça/box
                        foreach (var partAux in parts)
                        {
                            // Separa a o part number do número do box
                            var part = int.Parse(partAux.Substring(0, partAux.Length - 2));
                            var box = int.Parse(partAux.Substring(partAux.Length - 2));

                            // Inicia um objeto viewmodel
                            var model = new PickingViewModel { status = PickingStatus.Pendente };
                            var count = 1;

                            // Percorre cada chassi do grupo de 3
                            foreach (var c in group)
                            {
                                /* Encontra o chassi na lista filtrando por produto e box
                                   para pegar a quantidade necessária. */
                                var chassi = completeList.FirstOrDefault(x => x.produto == part && x.box == box && x.chassi == c);

                                /* Se não encontrou o chassi pelo produto e box, é porque o chassi não precisa da peça,
                                   então filtra apenas pelo chassi para saber a sequencia. */
                                if (chassi == null)
                                {
                                    chassi = completeList.First(x => x.chassi == c);
                                }

                                switch (count)
                                {
                                    // Coloca cada chassi em sua posição
                                    case 1:
                                        // Primeira vez seta as informações comuns entre os chassis
                                        model.estacao = chassi.estacao;
                                        model.modulo = chassi.modulo;
                                        model.box = box;
                                        model.dataProducao = chassi.dataProducao;
                                        model.produto = part;

                                        // Informações específicas da posição 1
                                        model.sequence01 = chassi.sequencia;
                                        model.chassi01 = chassi.chassi;
                                        if (chassi.produto == part) { model.quantidade01 = chassi.quantidade; }

                                        break;
                                    case 2:
                                        // Informações específicas da posição 2
                                        model.sequence02 = chassi.sequencia;
                                        model.chassi02 = chassi.chassi;
                                        if (chassi.produto == part) { model.quantidade02 = chassi.quantidade; }

                                        break;
                                    default:
                                        // Informações específicas da posição 3
                                        model.sequence03 = chassi.sequencia;
                                        model.chassi03 = chassi.chassi;
                                        if (chassi.produto == part) { model.quantidade03 = chassi.quantidade; }

                                        break;
                                }

                                count++;
                            }

                            // Apenas adiciona na lista os registros que tem alguma peça para coletar.
                            if (model.quantidade01 > 0 || model.quantidade02 > 0 || model.quantidade03 > 0)
                            {
                                pickingViewModelList.Add(model);
                            }
                        }
                    }
                }

                var sb = new StringBuilder();

                foreach (var dr in pickingViewModelList)
                {
                    sb.AppendLine("\"" + string.Join(",", dr.ToArray()).Replace(",", "\",\"") + "\"");
                }

                File.WriteAllText(txtFilePath.Text, sb.ToString());

                return true;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Erro", MessageBoxButtons.OK, MessageBoxIcon.Error);

                return false;
            }
        }

        private bool CreatePickingRecords()
        {
            if (dgOperators.SelectedRows.Count != 0)
            {
                _parametersList = new List<string[]>();

                var productionDate = dtpProductionDate.Value.ToString("MM/dd/yyyy");

                using (var conn = new OracleConnection(ConfigurationManager.ConnectionStrings["MyConnection"].ConnectionString))
                {
                    conn.Open();
                    var trans = conn.BeginTransaction(IsolationLevel.ReadCommitted);

                    try
                    {
                        foreach (DataGridViewRow row in dgKits.Rows)
                        {
                            var station = row.Cells["Station"].Value.ToString();
                            var module = row.Cells["Module"].Value.ToString();
                            var initialSequence = row.Cells["InitialSequence"].Value.ToString();

                            var cmd = new OracleCommand("pack_kit_interno.pr_pesq_chassi", conn)
                            {
                                CommandType = CommandType.StoredProcedure
                            };
                            cmd.Parameters.Add("p_cd_estacao_ini", OracleDbType.Varchar2).Value = station;
                            cmd.Parameters.Add("p_cd_estacao_fim", OracleDbType.Varchar2).Value = station;
                            cmd.Parameters.Add("p_nu_modulo", OracleDbType.Varchar2).Value = module;
                            cmd.Parameters.Add("p_dt_separacao_ini", OracleDbType.Date).Value = productionDate;
                            cmd.Parameters.Add("p_dt_separacao_fim", OracleDbType.Date).Value = productionDate;
                            cmd.Parameters.Add("p_cd_predio", OracleDbType.Char).Value = "W";
                            cmd.Parameters.Add("p_seq_chassi_ini_a", OracleDbType.Int32).Value = null;
                            cmd.Parameters.Add("p_seq_chassi_ini_z", OracleDbType.Int32).Value = null;
                            cmd.Parameters.Add("p_seq_chassi_fim_a", OracleDbType.Int32).Value = null;
                            cmd.Parameters.Add("p_seq_chassi_fim_z", OracleDbType.Int32).Value = null;
                            cmd.Parameters.Add("p_cd_familia_kit", OracleDbType.Int32).Value = 0;
                            cmd.Parameters.Add("p_flg_campos", OracleDbType.Varchar2).Value = "D";
                            cmd.Parameters.Add("p_flg_opcao", OracleDbType.Varchar2).Value = "C";
                            cmd.Parameters.Add("p_nu_kit", OracleDbType.Int32).Direction = ParameterDirection.Output;

                            cmd.Transaction = trans;
                            cmd.ExecuteNonQuery();

                            var nuKit = cmd.Parameters["p_nu_kit"].Value;

                            _parametersList.Add(new[] { station, module, productionDate, nuKit.ToString(), initialSequence });
                        }

                        trans.Commit();
                    }
                    catch (Exception ex)
                    {
                        trans.Rollback();

                        MessageBox.Show(ex.Message, "Erro", MessageBoxButtons.OK, MessageBoxIcon.Error);

                        return false;
                    }
                }

                return true;
            }

            MessageBox.Show("Selecione um operador!", "Erro", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);

            return false;
        }
    }

    public class Picking
    {
        public string dataProducao { get; set; }
        public int estacao { get; set; }
        public int modulo { get; set; }
        public int sequencia { get; set; }
        public int chassi { get; set; }
        public int box { get; set; }
        public int produto { get; set; }
        public int quantidade { get; set; }
        public PickingStatus status { get; set; }

        public Picking(string dataProducao, int estacao, int modulo, int sequencia, int chassi, int box, int produto, int quantidade, PickingStatus status)
        {
            this.dataProducao = dataProducao;
            this.estacao = estacao;
            this.modulo = modulo;
            this.sequencia = sequencia;
            this.chassi = chassi;
            this.box = box;
            this.produto = produto;
            this.quantidade = quantidade;
            this.status = status;
        }
    }

    public enum PickingStatus
    {
        Coletando, Finalizado, Pendente
    }

    public class PickingViewModel
    {
        public long id { get; set; }
        public int estacao { get; set; }
        public int modulo { get; set; }
        public int box { get; set; }
        public int produto { get; set; }
        public string dataProducao { get; set; }
        public int chassi01 { get; set; }
        public int quantidade01 { get; set; }
        public int chassi02 { get; set; }
        public int quantidade02 { get; set; }
        public int chassi03 { get; set; }
        public int quantidade03 { get; set; }
        public PickingStatus status { get; set; }
        public int sequence01 { get; set; }
        public int sequence02 { get; set; }
        public int sequence03 { get; set; }

        public PickingViewModel()
        {
        }

        public PickingViewModel(long id,
                                int estacao,
                                int modulo,
                                int box,
                                int produto,
                                string dataProducao,
                                int chassi01,
                                int quantidade01,
                                int sequence01,
                                int chassi02,
                                int quantidade02,
                                int sequence02,
                                int chassi03,
                                int quantidade03,
                                int sequence03,
                                PickingStatus status)
        {
            this.id = id;
            this.estacao = estacao;
            this.modulo = modulo;
            this.box = box;
            this.produto = produto;
            this.dataProducao = dataProducao;
            this.chassi01 = chassi01;
            this.quantidade01 = quantidade01;
            this.chassi02 = chassi02;
            this.quantidade02 = quantidade02;
            this.chassi03 = chassi03;
            this.quantidade03 = quantidade03;
            this.status = status;
            this.sequence01 = sequence01;
            this.sequence02 = sequence02;
            this.sequence03 = sequence03;
        }

        public string[] ToArray()
        {
            return new string[]
            {
                this.estacao.ToString(),
                this.modulo.ToString(),
                this.box.ToString(),
                this.produto.ToString(),
                this.dataProducao,
                this.chassi01.ToString(),
                this.quantidade01.ToString(),
                this.chassi02.ToString(),
                this.quantidade02.ToString(),
                this.chassi03.ToString(),
                this.quantidade03.ToString(),
                this.status.ToString(),
                this.sequence01.ToString(),
                this.sequence02.ToString(),
                this.sequence03.ToString()
            };
        }
    }

}
