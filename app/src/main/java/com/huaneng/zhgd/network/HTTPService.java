package com.huaneng.zhgd.network;

import com.huaneng.zhgd.bean.Answer;
import com.huaneng.zhgd.bean.AppVersion;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.bean.Attachment;
import com.huaneng.zhgd.bean.AttendanceCompany;
import com.huaneng.zhgd.bean.Entry;
import com.huaneng.zhgd.bean.ImportDeviceInfo;
import com.huaneng.zhgd.bean.InviteRoom;
import com.huaneng.zhgd.bean.JoinMeeting;
import com.huaneng.zhgd.bean.MyMenu;
import com.huaneng.zhgd.bean.News;
import com.huaneng.zhgd.bean.Notice;
import com.huaneng.zhgd.bean.PopWindowMenu;
import com.huaneng.zhgd.bean.Question;
import com.huaneng.zhgd.bean.SpecialWork;
import com.huaneng.zhgd.bean.TestQuestion;
import com.huaneng.zhgd.bean.UploadImagesResult;
import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.bean.Vehicel;
import com.huaneng.zhgd.modules.Company;
import com.huaneng.zhgd.modules.CompanyInfo;
import com.huaneng.zhgd.warehouse.Warehouse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by TH on 2017/11/8.
 */

public interface HTTPService {

    @FormUrlEncoded
    @POST
    Observable<Response> post(@Url String url, @FieldMap Map<String,Object> map);

    @GET
    Observable<Response<News>> getAttachment(@Url String url, @Query("id") String id);

    @GET
    Observable<Response<List<ImportDeviceInfo>>> getImportDeviceInfo(@Url String url);

    @GET
    Observable<Response> get(@Url String url);

    @GET
    Observable<Response> get(@Url String url, @QueryMap Map<String, Object> map);

    @GET("schoolfile/list")
    Observable<Response<List<Attachment>>> schoolfileList(@Query("type") String type);

    @FormUrlEncoded
    @POST("schoolexam/save")
    Observable<Response<List<Answer>>> schoolexamSave(@Field("questionid") String questionid, @Field("answer") String answer, @Field("userid") String userid);

    @GET("schoolexam/read")
    Observable<Response<List<Question>>> schoolexamRead(@Query("size") int size, @Query("id") String id);

    @GET("schoolexam/list")
    Observable<Response<List<TestQuestion>>> getSchoolexamList(@Query("userid") String userid);

    @FormUrlEncoded
    @POST("person/post")
    Observable<Response<User>> login(@Field("userid") String userid, @Field("password") String password);

    // 三十五： 获取用户信息
    @GET("person/getinfo")
    Observable<Response<User>> getinfo();

    @GET
    Observable<Response<List<Article>>> getArticleList(@Url String url, @Query("title") String title, @Query("norm") String norm, @Query("major") String major, @Query("companyid") String companyid);

    @GET
    Observable<Response<List<Article>>> getCompanies(@Url String url, @Query("companyid") String companyid);

    @GET
    Observable<Response<ImportDeviceInfo>> getImportDeviceInfo(@Url String url, @Query("id") String id);

    @GET("users/getlist")
    Observable<Response<List<AttendanceCompany>>> getAttendanceList(@Query("company_id") String company_id);

    @GET("backconfig/getselect")
    Observable<Response<List<PopWindowMenu>>> getselect(@Query("type") String type);

    @GET
    Observable<Response<Article>> getArticle(@Url String url, @Query("id") String id);

    // 仓库
    @GET
    Observable<Response<List<Warehouse>>> warehouse(@Url String url);

    // 十八 获取单位列表
    @GET("company/getlist")
    Observable<Response<List<CompanyInfo>>> getCompanies();

    // 获取人员列表
    @GET
    Observable<Response<List<Company>>> getCompanyList(@Url String url);

    // 获取当前登陆者要开的会议
    // 参数：keyword    可选    关键词搜索
    @GET("message/getproom")
    Observable<Response<List<JoinMeeting>>> getproom();

    @GET("message/getproom")
    Observable<Response<List<JoinMeeting>>> getproom(@Query("markid") String markid);

    @FormUrlEncoded
    @POST("message/post")
    Observable<Response<InviteRoom>> sendMessage(@Field("personid[]") String[] personids, @Field("message") String message, @Field("createid") String createid);

    // 六．获取房间状态
    @FormUrlEncoded
    @POST("message/get")
    Observable<Response> getRoomStatus(@Field("roomid") String roomid);

    // 房间无人时，删除房间
    @FormUrlEncoded
    @POST("message/del")
    Observable<Response> delRoom(@Field("roomid") String roomid, @Field("markid") String markid, @Field("message") String message);

    // 八.获取根据人员markid 获取个人菜单菜单
    @FormUrlEncoded
    @POST("menu/getmenu")
    Observable<Response<MyMenu>> getmenu(@Field("markid") String markid);

    @GET("backconfig/getlist")
    Observable<Response<AppVersion>> getAppVersion(@Query("type") String type);

    @GET("notice/getlist/1")
    Observable<Response<List<Notice>>> getNotices();

    @GET("notice/getcount")
    Observable<Response<List<Notice>>> getNewNotice(@Query("count") int count);

    // 修改密码
    @FormUrlEncoded
    @POST("person/updatepwd")
    Observable<Response> updatepwd(@Field("beforepwd") String beforepwd, @Field("nowpwd") String nowpwd, @Field("markid") String markid);

    // 二十 ：图片上传接口
    @Multipart
    @POST("image/save")
//    Observable<Response<UploadImagesResult>> saveImages(@PartMap Map<String, RequestBody> files);
    Observable<Response> saveImages(@PartMap Map<String, RequestBody> files);

    // 三十一： 更改头像
    @FormUrlEncoded
    @POST("person/updateimage")
    Observable<Response> updateimage(@Field("images") String images);

    //设备管理：进场数据录入
    @GET
    Observable<Response<List<Entry>>> getEntryList(@Url String url, @Query("id") String id, @Query("page") String page, @Query("pagesize") String pagesize);

    //设备管理：特殊工种
    @GET
    Observable<Response<List<SpecialWork>>> getWorkList(@Url String url, @Query("id") String id, @Query("page") String page, @Query("pagesize") String pagesize);

    //设备管理：现场流动机械
    @GET
    Observable<Response<List<Vehicel>>> getVehicelList(@Url String url, @Query("id") String id, @Query("page") String page, @Query("pagesize") String pagesize);
}
