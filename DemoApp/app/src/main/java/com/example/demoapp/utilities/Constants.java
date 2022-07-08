package com.example.demoapp.utilities;

import java.util.HashMap;

public class Constants {


    public static final String URL_API = "http://192.168.1.89/database/";


    public static final String FCL_UPDATE = "update_fcl";

    public static final String FCL_OBJECT = "object_fcl";
    public static final String FCL_ADD_NEW = "add_new_fcl";

    public static final String IMPORT_OBJECT = "object_imp";
    public static final String IMPORT_UPDATE = "add_new_import";
    public static final String IMPORT_ADD_NEW = "update_import";

    public static final String IMPORT_LCL_OBJECT = "object_imp_lcl";
    public static final String IMPORT_LCL_UPDATE = "add_new_import_lcl";
    public static final String IMPORT_LCL_ADD_NEW = "update_import_lcl";

    public static final String DOM_EXPORT_OBJECT = "object_dom_export";
    public static final String DOM_EXPORT_UPDATE = "add_new_dom_export";
    public static final String DOM_EXPORT_ADD_NEW = "update_dom_export";

    public static final String DOM_IMPORT_OBJECT = "object_dom_import";
    public static final String DOM_IMPORT_UPDATE = "add_new_dom_import";
    public static final String DOM_IMPORT_ADD_NEW = "update_dom_import";

    public static final String DOM_DRY_OBJECT = "object_dom_dry";
    public static final String DOM_DRY_UPDATE = "add_new_dom_dry";
    public static final String DOM_DRY_ADD_NEW = "update_dom_dry";

    public static final String DOM_COLD_OBJECT = "object_dom_cold";
    public static final String DOM_COLD_UPDATE = "add_new_dom_cold";
    public static final String DOM_COLD_ADD_NEW = "update_dom_cold";


    public static final String DOM_CY_OBJECT = "object_dom_cy";
    public static final String DOM_CY_UPDATE = "add_new_dom_cy";
    public static final String DOM_CY_ADD_NEW = "update_dom_cy";

    public static final String DOM_CY_SEA_OBJECT = "object_dom_cy_sea";
    public static final String DOM_CY_SEA_UPDATE = "add_new_dom_cy_sea";
    public static final String DOM_CY_SEA_ADD_NEW = "update_dom_cy_sea";

    public static final String DOM_DOOR_OBJECT = "object_dom_door";
    public static final String DOM_DOOR_UPDATE = "add_new_dom_door";
    public static final String DOM_DOOR_ADD_NEW = "update_dom_door";

    public static final String DOM_DOOR_SEA_OBJECT = "object_dom_door_sea";
    public static final String DOM_DOOR_SEA_UPDATE = "add_new_dom_door_sea";
    public static final String DOM_DOOR_SEA_ADD_NEW = "update_dom_door_sea";

    public static final String RETAIL_GOODS = "object_retail_goods" ;
    public static final String RETAIL_GOODS_UPDATE = "object_retail_goods_update";




    public  final static String[] ITEMS_MONTH = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7",
            "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};

    public  final static String[] ITEMS_TYPE_DOM_DRY = {"500KGS", "1 Tấn", "2 Tấn", "3 Tấn", "5 Tấn", "8 Tấn", "10 Tấn", "13 Tấn", "15 Tấn" };

    public  final static String[] ITEMS_CONTINENT = {"Asia", "Europe", "America", "Africa", "Australia"};

    public  final static String[] ITEMS_CARGO = {"DG", "NON-DG"};
    public  final static String[] ITEMS_DOM_CY = {"20FT", "40FT"};

    public final static  String[] ITEMS_IMPORTANDEXPORT = {"Nhập Khẩu", "Xuất Khẩu"};

    public  final static String[] ITEMS_FCL = {"GP", "FR", "RF", "OT", "HC"};

    public  final static String[] ITEMS_DOM = {"FT", "RF", "OT", "FR", "ISO"};

    public  final static String[] ITEMS_DOM_SEA = {"FT", "RF"};


    public final static String[] ITEMS_IMPORT = {"GP", "HQ", "RF", "FR", "OT", "TK"};



    public static final String LOG_OBJECT = "object_log";
    public static final String LOG_UPDATE = "object_update";
    public static final String AIR_OBJECT = "object_air";
    public static final String AIR_IMPORT = "object_import_air";
    public static final String AIR_IMPORT_UPDATE = "object_import_air_update";
    public static final String AIR_IMPORT_INSERT = "object_import_air_insert";
    public static final String AIR_UPDATE = "update_air";

    public final  static String[] ITEMS_TYPE = {"Xuất kinh doanh", "Xuất gia công", "Xuất SXXK", "Xuất PMD Cty", "Xuất PMD CN",
    "Xuất tại chỗ", "XN tại chỗ", "Di lý", "Quá cảnh", "Nhập kinh doanh", "Nhập gia công", "Nhập SXXK", "Nhập PMD Cty",
    "Nhập PMD CN", "Nhập tại chỗ", "Giấy phép", "Vận chuyển", "Kiểm định", "Kiểm dịch", "Xin C/O",
    "Kiểm đếm", "Thanh lý", "Lashing", "Tái xuất", "Thay remark"};


    public static final String ERROR_AUTO_COMPLETE_MONTH = "Bạn chưa chọn tháng !!!";
    public static final String ERROR_AUTO_COMPLETE_CONTINENT = "Bạn chưa chọn châu !!!";
    public static final String ERROR_AUTO_COMPLETE_SHIPPING_TYPE = "Bạn chưa chọn loại vận chuyển!!!";
    public static final String ERROR_AUTO_COMPLETE_TYPE_LOG = "Bạn chưa chọn loại vận chuyển!!!";
    public static final String ERROR_AUTO_COMPLETE_TYPE = "Bạn chưa chọn loại cont !!!";

    public static final String ERROR_POL = "Bạn chưa nhập điểm đi!!";
    public static final String ERROR_POD = "Bạn chưa nhập điểm đến!!";
    public static final String ERROR_VALID = "Bạn chưa nhập Valid!!";

    public static final String INSERT_FAILED = "Insert Failed!!!";
    public static final String UPDATE_FAILED = "Update Failed!!!";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";

    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";

    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";

    public static final  String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final  String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final  String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    public static HashMap<String,String> getRemoteMessageHeaders()
    {
        HashMap<String ,String> headers = new HashMap<>();
        headers.put(Constants.REMOTE_MSG_AUTHORIZATION,"key=AAAAnDxU0iU:APA91bGl1M-g_K_E43PrHhOo7Am4lu6gvqNN_NcmiAbR55gryt67ABv2KwCzNK9oKOxkpsgSm-RCmiaDTrKDehmVwM576cL15pJ5pX0s5QWf-RlIP8HVdO01BkVMQw9oua2RDQ_Sxw8B");
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }


}
