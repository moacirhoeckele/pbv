namespace CsvGenerator
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            this.dgOperators = new System.Windows.Forms.DataGridView();
            this.dgKits = new System.Windows.Forms.DataGridView();
            this.btnCreate = new System.Windows.Forms.Button();
            this.dtpProductionDate = new System.Windows.Forms.DateTimePicker();
            this.lblProductionDate = new System.Windows.Forms.Label();
            this.lblFilePath = new System.Windows.Forms.Label();
            this.txtFilePath = new System.Windows.Forms.TextBox();
            this.fbdFilePath = new System.Windows.Forms.FolderBrowserDialog();
            this.btnFind = new System.Windows.Forms.Button();
            this.txtLog = new System.Windows.Forms.TextBox();
            ((System.ComponentModel.ISupportInitialize)(this.dgOperators)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.dgKits)).BeginInit();
            this.SuspendLayout();
            // 
            // dgOperators
            // 
            this.dgOperators.AllowUserToAddRows = false;
            this.dgOperators.AllowUserToDeleteRows = false;
            this.dgOperators.AllowUserToResizeColumns = false;
            this.dgOperators.AllowUserToResizeRows = false;
            this.dgOperators.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgOperators.Location = new System.Drawing.Point(12, 12);
            this.dgOperators.MultiSelect = false;
            this.dgOperators.Name = "dgOperators";
            this.dgOperators.ReadOnly = true;
            this.dgOperators.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgOperators.Size = new System.Drawing.Size(209, 145);
            this.dgOperators.TabIndex = 0;
            this.dgOperators.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.DgOperators_RowEnter);
            // 
            // dgKits
            // 
            this.dgKits.AllowUserToAddRows = false;
            this.dgKits.AllowUserToDeleteRows = false;
            this.dgKits.AllowUserToResizeColumns = false;
            this.dgKits.AllowUserToResizeRows = false;
            this.dgKits.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgKits.Location = new System.Drawing.Point(227, 12);
            this.dgKits.MultiSelect = false;
            this.dgKits.Name = "dgKits";
            this.dgKits.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.CellSelect;
            this.dgKits.Size = new System.Drawing.Size(497, 145);
            this.dgKits.TabIndex = 1;
            // 
            // btnCreate
            // 
            this.btnCreate.Location = new System.Drawing.Point(604, 277);
            this.btnCreate.Name = "btnCreate";
            this.btnCreate.Size = new System.Drawing.Size(120, 41);
            this.btnCreate.TabIndex = 2;
            this.btnCreate.Text = "Gerar";
            this.btnCreate.UseVisualStyleBackColor = true;
            this.btnCreate.Click += new System.EventHandler(this.BtnCreate_Click);
            // 
            // dtpProductionDate
            // 
            this.dtpProductionDate.CustomFormat = "dd/MM/yyyy";
            this.dtpProductionDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
            this.dtpProductionDate.Location = new System.Drawing.Point(12, 186);
            this.dtpProductionDate.Name = "dtpProductionDate";
            this.dtpProductionDate.Size = new System.Drawing.Size(112, 20);
            this.dtpProductionDate.TabIndex = 3;
            // 
            // lblProductionDate
            // 
            this.lblProductionDate.AutoSize = true;
            this.lblProductionDate.Location = new System.Drawing.Point(9, 170);
            this.lblProductionDate.Name = "lblProductionDate";
            this.lblProductionDate.Size = new System.Drawing.Size(79, 13);
            this.lblProductionDate.TabIndex = 4;
            this.lblProductionDate.Text = "Data Produção";
            // 
            // lblFilePath
            // 
            this.lblFilePath.AutoSize = true;
            this.lblFilePath.Location = new System.Drawing.Point(9, 221);
            this.lblFilePath.Name = "lblFilePath";
            this.lblFilePath.Size = new System.Drawing.Size(125, 13);
            this.lblFilePath.TabIndex = 5;
            this.lblFilePath.Text = "Caminho do arquivo CSV";
            // 
            // txtFilePath
            // 
            this.txtFilePath.Location = new System.Drawing.Point(12, 237);
            this.txtFilePath.Name = "txtFilePath";
            this.txtFilePath.Size = new System.Drawing.Size(313, 20);
            this.txtFilePath.TabIndex = 6;
            // 
            // fbdFilePath
            // 
            this.fbdFilePath.RootFolder = System.Environment.SpecialFolder.MyComputer;
            // 
            // btnFind
            // 
            this.btnFind.Location = new System.Drawing.Point(325, 236);
            this.btnFind.Name = "btnFind";
            this.btnFind.Size = new System.Drawing.Size(64, 22);
            this.btnFind.TabIndex = 7;
            this.btnFind.Text = "Procurar";
            this.btnFind.UseVisualStyleBackColor = true;
            this.btnFind.Click += new System.EventHandler(this.BtnFind_Click);
            // 
            // txtLog
            // 
            this.txtLog.Location = new System.Drawing.Point(15, 351);
            this.txtLog.Multiline = true;
            this.txtLog.Name = "txtLog";
            this.txtLog.ReadOnly = true;
            this.txtLog.Size = new System.Drawing.Size(639, 194);
            this.txtLog.TabIndex = 9;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(736, 330);
            this.Controls.Add(this.txtLog);
            this.Controls.Add(this.btnFind);
            this.Controls.Add(this.txtFilePath);
            this.Controls.Add(this.lblFilePath);
            this.Controls.Add(this.lblProductionDate);
            this.Controls.Add(this.dtpProductionDate);
            this.Controls.Add(this.btnCreate);
            this.Controls.Add(this.dgKits);
            this.Controls.Add(this.dgOperators);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "Form1";
            this.Text = "CSV Generator";
            this.Load += new System.EventHandler(this.Form1_Load);
            ((System.ComponentModel.ISupportInitialize)(this.dgOperators)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.dgKits)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.DataGridView dgOperators;
        private System.Windows.Forms.DataGridView dgKits;
        private System.Windows.Forms.Button btnCreate;
        private System.Windows.Forms.DateTimePicker dtpProductionDate;
        private System.Windows.Forms.Label lblProductionDate;
        private System.Windows.Forms.Label lblFilePath;
        private System.Windows.Forms.TextBox txtFilePath;
        private System.Windows.Forms.FolderBrowserDialog fbdFilePath;
        private System.Windows.Forms.Button btnFind;
        private System.Windows.Forms.TextBox txtLog;
    }
}

