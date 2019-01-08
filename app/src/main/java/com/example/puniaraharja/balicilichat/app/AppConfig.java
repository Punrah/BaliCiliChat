package com.example.puniaraharja.balicilichat.app;

public class AppConfig {


    private  static String  general = "http://balicili.com/";
    public static final String URL_CATEGORY = general+"json_main.php?aksi=list_category";
    public static final String URL_ITEM = general+"json_main.php?aksi=list_item";;
	public static final String URL_IMAGE = "http://caramenghilangkanjerawatdanbekasnya.info/wp-content/uploads/2015/06/Manfaat-Buah-Tomat-Untuk-Kecantikan-Kulit-dan-Wajah.jpg";
	public static final String URL_DRIVER_IMAGE = "";
	public static final String URL_LOGIN =general+"action.php?aksi=login";
    public static final String URL_REGISTER = "";
    public static final String URL_VERIFY = "";


    public static String getWebURL(String webAction) {
        return "http://www.balicili.com/";
    }


    public static String getEventUrl() {
        return general+"admin/modul/event/action.php?aksi=list_event";
    }
    public static String getProductUrl() {
        return general+"admin/modul/product/action.php?aksi=list_product";
    }
}
