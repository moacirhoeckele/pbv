package com.volvo.wis.pbv.services;

import com.volvo.wis.pbv.viewmodels.KitViewModel;
import com.volvo.wis.pbv.viewmodels.OperationResult;
import com.volvo.wis.pbv.viewmodels.OperationResultSingle;
import com.volvo.wis.pbv.viewmodels.PickingViewModel;

public interface IPickingService {

    OperationResult LoadData();

    OperationResultSingle<KitViewModel> ValidateKit(String json);

    OperationResultSingle<PickingViewModel> LoadNextPicking(Integer station, Integer module);

    OperationResult SetAsPending(long id);

    OperationResult FinishPicking(long id);
}
