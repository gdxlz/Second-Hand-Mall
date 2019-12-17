package com.hzf.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hzf.entity.*;
import com.hzf.service.GoodService;
import com.hzf.service.PictureService;
import com.hzf.service.TimeRecordService;
import com.hzf.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/good")
public class GoodController {

    @Autowired
    private GoodService goodService;
    @Autowired
    private UserService userService;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private TimeRecordService timeRecordService;
// 查找所有的商品的信息以及一张图片
    @ResponseBody
    @GetMapping("/queryAllGoodPicture")
    public PageInfo<GoodPicture> queryAllGoodPicture(
            @RequestParam(value="pageNo",required=false,defaultValue="1")int pageNo,
            @RequestParam(value="pageSize",required=false,defaultValue="8")int pageSize,
            HttpServletRequest request)
    {
        System.out.println("xixi");
        if(pageNo<=0){
            pageNo=1;
        }
        PageHelper.startPage(pageNo,pageSize);
        User user = (User)request.getSession().getAttribute("user");
        System.out.println(user);
        List<GoodPicture> list;
        list = goodService.findAllGoodPicture();
        PageInfo<GoodPicture> pageInfo = new PageInfo<>(list);
        for (GoodPicture goodPicture:list
                ) {
            System.out.println(goodPicture);
        }
        return pageInfo;
    }

    // 根据用户的收藏查找所有的商品的信息以及一张图片(推荐功能)
    @ResponseBody
    @GetMapping("/queryFavoriteGoodPicture")
    public PageInfo<GoodPicture> queryFavoriteGoodPicture(
            @RequestParam(value="pageNo",required=false,defaultValue="1")int pageNo,
            @RequestParam(value="pageSize",required=false,defaultValue="8")int pageSize,
            HttpServletRequest request)
    {
        System.out.println("haha");
        if(pageNo<=0){
            pageNo=1;
        }
        PageHelper.startPage(pageNo,pageSize);
        User user = (User)request.getSession().getAttribute("user");
        System.out.println(user);
        List<GoodPicture> list;
        //获取当前用户的收藏
       // List<Collect> userCollect=goodService.getAllCollectByUid(user.getUid());
//       如果用户没有登录，则按照good_id降序显示
        if (user==null){
            list = goodService.findAllGoodPicture();
        } else if(goodService.getAllCollectByUid(user.getUid()) == null){
//      如果用户登录了，但是没有收藏东西，则也默认显示全部

                System.out.println("用户没有收藏东西时，userCollect是空的，也默认返回全部");
                list = goodService.findAllGoodPicture();
            }else{
//      用户不仅登录了，也收藏了东西，按照爱好进行推荐
            list = goodService.findAllGoodPictureByUserFavotite(user.getUid());
        }
        PageInfo<GoodPicture> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    // 根据用户输入的信息进行模糊查询
    @ResponseBody
    @GetMapping("/findAllGoodPictureByString")
    public PageInfo<GoodPicture> findAllGoodPictureByString(
            @RequestParam(value="pageNo",required=false,defaultValue="1")int pageNo,
            @RequestParam(value="pageSize",required=false,defaultValue="8")int pageSize,
            @RequestParam(value = "str", required = false) String str)
    {
        System.out.println(str);
        if(pageNo<=0){
            pageNo=1;
        }
        PageHelper.startPage(pageNo,pageSize);
        List<GoodPicture> list;
        //str = "生活";
        list = goodService.findAllGoodPictureByString(str);
        PageInfo<GoodPicture> pageInfo = new PageInfo<>(list);
        for (GoodPicture goodPicture:list
                ) {
            System.out.println(goodPicture);
        }
        return pageInfo;
    }

//    根据用户点击输入的id查找商品，以及相关的用户，图片信息
    @ResponseBody
    @RequestMapping(value="/showGoodDetail/{goodId}",method=RequestMethod.GET)
    public GoodDetail showGoodDetail(@PathVariable Integer goodId){
        System.out.println(goodId);
//    通过商品id获取商品信息
        Good good = goodService.findGoodById(goodId);
        int uid = good.getUserId();
        System.out.println(uid);
//     通过商品的用户id查找用户的详细信息
        User user = userService.findUserById(uid);
//    通过商品的id获取图片信息
        List<Pictures> list= pictureService.findPictureById(goodId);
       GoodDetail goodDetail = new GoodDetail();
       goodDetail.setGood(good);
       goodDetail.setPicturesList(list);
       goodDetail.setUid(uid);
       goodDetail.setUserName(user.getUsername());
       goodDetail.setTelephone(user.getTelephone());
        System.out.println(good);
        System.out.println(user);
        for (Pictures pic:list
             ) {
            System.out.println(pic);
        }
        return goodDetail;
    }

//上传商品
    @RequestMapping("/saveGood")
    public String saveGood(HttpServletRequest request, Good good2, Pictures ima, MultipartFile pic){

        User cur_user = (User)request.getSession().getAttribute("user");
        int uid=cur_user.getUid();
        good2.setUserId(uid);
        good2.setStatus(9);
        System.out.println(good2);
        goodService.saveGood(good2);

       // String path=request.getSession().getServletContext().getRealPath("/uploads/");

        String path = System.getProperty("user.dir");
//        String picturePath = path + "\\src\\main\\resources\\static\\goodPicture";
        String picturePath = path + "\\target\\classes\\static\\goodPicture";
        File file=new File(picturePath);
        if (!file.exists()){
            file.mkdir();
        }

        String picname=pic.getOriginalFilename();
//部分浏览器上传照片时将完整路径也上传的情况下，只获取文件最后的名字
        int unixSep = picname.lastIndexOf('/');
        int winSep = picname.lastIndexOf('\\');
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1)  {
            picname = picname.substring(pos + 1);
        }
       // picname = picname.substring(picname.lastIndexOf("\\")+1);
        String uuid= UUID.randomUUID().toString().replace("-","");
        picname=uuid+"_"+picname;
        System.out.println(picname+"---------");
        System.out.println(picturePath);
        try {
            pic.transferTo(new File(picturePath,picname));

        } catch (IOException e) {
            e.printStackTrace();
        }

//      获得最新的商品的id
        int gid=goodService.findLastGood();
        System.out.println(gid);
        //       图片表插入信息
        Pictures pictures=new Pictures();
        pictures.setGoodId(gid);
        pictures.setPictureUrl(picname);
        System.out.println(pictures);
        pictureService.savePic(pictures);
//      时间记录表插入信息
        System.out.println("插入时间记录");
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setGoodId(gid);
        timeRecord.setUptime(new Date());
        System.out.println(timeRecord);
        timeRecordService.saveTimeRecord(timeRecord);

        return "redirect:/hello/index";
    }

//    商品详情页面的点击“收藏”即可收藏商品的功能
    @RequestMapping(value = "/saveCollect/{goodId}",method = RequestMethod.GET)
    public String saveCollect(@PathVariable Integer goodId ,HttpServletRequest request){
        //获取当前登陆的用户和用户id
        System.out.println(goodId);
        User cur_user = (User)request.getSession().getAttribute("user");
        //如果用户没有登录则跳转到登录界面
//        if(cur_user == null){
//            System.out.println("aaaaa");
//            //return "redirect:/hello/login.html";
//        }
        int uid=cur_user.getUid();
        //获取当前用户的收藏
        List<Collect> userCollect=goodService.getAllCollectByUid(uid);
        //如果用户没有任何收藏则直接把商品加入收藏
        if (userCollect.isEmpty()){
            Collect collect=new Collect();
            collect.setUserId(uid);
            collect.setGoodId(goodId);
            goodService.saveCollect(collect);
//        收藏成功则返回到：“我的收藏”
            return "redirect:/hello/collection1";
        }
        //遍历用户的收藏
        for (Collect tmpcollect:userCollect){
            int temgoodid=tmpcollect.getGoodId();
            if(temgoodid==goodId.intValue()){
//        之前收藏过也返回到：“我的收藏”
               return "redirect:/hello/collection1";
            }
        }
        Collect collect=new Collect();
        collect.setUserId(uid);
        collect.setGoodId(goodId);
        goodService.saveCollect(collect);
//       之前没有收藏过，但是现在收藏了，也返回到：“我的收藏”
        return "redirect:/hello/collection1";
    }

//    删除收藏了的商品
    @RequestMapping(value = "/deleteCollection/{goodId}",method = RequestMethod.GET)
    public String deleteCollection(@PathVariable(value = "goodId") Integer goodId,HttpServletRequest request){
        //获取当前登陆的用户和用户id
        User cur_user = (User)request.getSession().getAttribute("user");
        int uid=cur_user.getUid();
        // 通过用户id和商品id删除
        goodService.deleteCollection(uid,goodId);
        return "redirect:/hello/collection1";
    }

    // 显示我收藏的商品
    @ResponseBody
    @GetMapping("/findMyCollections")
    public PageInfo<GoodCollection> findMyCollections(
            @RequestParam(value="pageNo",required=false,defaultValue="1")int pageNo,
            @RequestParam(value="pageSize",required=false,defaultValue="4")int pageSize,
            HttpServletRequest request
    )
    {   PageInfo<GoodCollection> goodCollectionListPageInfo=null;
        if(pageNo<=0){
            pageNo=1;
        }
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
            return goodCollectionListPageInfo;
        }
        int uid = user.getUid();
//   查找当前用户收藏了的商品的所有id
        PageHelper.startPage(pageNo,pageSize);
        List<Collect> collectList = goodService.findGoodIdByUser(uid);
        PageInfo<Collect> pageInfo = new PageInfo<>(collectList);
//        List<Integer> list = new ArrayList<>();
//        int goodId;
//        for (int i=0;i<collectList.size();i++){
//            goodId=collectList.get(i).getGoodId();
//            list.add(goodId);
//        }
        List<Integer> goodIds = pageInfo.getList().stream().map(Collect::getGoodId).collect(Collectors.toList());
        System.out.println("当前登录的用户收藏的商品的id:");
        for (Integer i :goodIds){
            System.out.println(i);
        }
        List<Pictures> picturesList;
//    如果没有导入java.util.List;,就会报错cannot resolve symbol ‘ArrayList’，要注意！
        List<GoodCollection> goodCollectionList = new ArrayList<>();
        Good good;
        User seller;
        int goodId;
        for (int i=0;i<collectList.size();i++){
//      这个一定不能拿到for外面，否则只会创建一个对象，导致list里面的对象就会和最后一个更改的对象一模一样
            GoodCollection goodCollection = new GoodCollection();
            goodId = goodIds.get(i);
            goodCollection.setGoodId(goodId);
//    通过goodId查找picturesList，取第一张图片
            picturesList=pictureService.findPictureById(goodId);
            goodCollection.setPictureUrl(picturesList.get(0).getPictureUrl());
//     通过goodId查找Good 对象
            good = goodService.findGoodById(goodId);
            goodCollection.setDescription(good.getDescription());
            goodCollection.setPrice(good.getPrice());
//      通过商品的userId查找卖家的username;
            int sellerId = good.getUserId();
            seller = userService.findUserById(sellerId);
            goodCollection.setUserName(seller.getUsername());
            goodCollectionList.add(goodCollection);
        }
        goodCollectionListPageInfo = new PageInfo<>(goodCollectionList);
        BeanUtils.copyProperties(pageInfo,goodCollectionListPageInfo);
        goodCollectionListPageInfo.setList(goodCollectionList);
        goodCollectionList.forEach(System.out::println);
//   注意：这里返回的是Collection类型的pageInfo
        System.out.println(goodCollectionListPageInfo);
        return goodCollectionListPageInfo;
    }

    //  显示我发布但是还在审核中的商品
    @ResponseBody
    @GetMapping("/queryAllMyCheckingGoodAndPicture")
    public PageInfo<GoodPicture> queryAllMyCheckingGoodAndPicture(
            @RequestParam(value="pageNo",required=false,defaultValue="1")int pageNo,
            @RequestParam(value="pageSize",required=false,defaultValue="4")int pageSize,
            HttpServletRequest request)
    {   PageInfo<GoodPicture> pageInfo = null;
        if(pageNo<=0){
            pageNo=1;
        }
        PageHelper.startPage(pageNo,pageSize);
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
//            没有登录，跳到登录界面
            return pageInfo;
        }
        System.out.println(user);
        Integer uid = user.getUid();
        List<GoodPicture> list;
        //  查询该用户发布过的，还没有通过审核（状态为9）的所有商品以及对应的一张图片
        list = goodService.findAllMyGoodAndPictureByUidAndStatus(uid,9);
        pageInfo = new PageInfo<>(list);
        for (GoodPicture goodPicture:list
                ) {
            System.out.println(goodPicture);
        }
        return pageInfo;
    }

//  显示我发布并且还在售的商品
    @ResponseBody
    @GetMapping("/queryAllMyGoodAndPicture")
    public PageInfo<GoodPicture> queryAllMyGoodAndPicture(
            @RequestParam(value="pageNo",required=false,defaultValue="1")int pageNo,
            @RequestParam(value="pageSize",required=false,defaultValue="4")int pageSize,
            HttpServletRequest request)
    {   PageInfo<GoodPicture> pageInfo = null;
        if(pageNo<=0){
            pageNo=1;
        }
        PageHelper.startPage(pageNo,pageSize);
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
//            没有登录，跳到登录界面
            return pageInfo;
        }
        System.out.println(user);
        Integer uid = user.getUid();
        List<GoodPicture> list;
    //  查询该用户发布过的，还在售（状态为0）的所有商品以及对应的一张图片
        list = goodService.findAllMyGoodAndPictureByUidAndStatus(uid,0);
        pageInfo = new PageInfo<>(list);
        for (GoodPicture goodPicture:list
                ) {
            System.out.println(goodPicture);
        }
        return pageInfo;
    }

    //  显示我发布并且但是已下架的商品
    @ResponseBody
    @GetMapping("/queryAllMyDownGoodAndPicture")
    public PageInfo<GoodPicture> queryAllMyDownGoodAndPicture(
            @RequestParam(value="pageNo",required=false,defaultValue="1")int pageNo,
            @RequestParam(value="pageSize",required=false,defaultValue="4")int pageSize,
            HttpServletRequest request)
    {   PageInfo<GoodPicture> pageInfo = null;
        if(pageNo<=0){
            pageNo=1;
        }
        PageHelper.startPage(pageNo,pageSize);
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
//            没有登录，跳到登录界面
            return pageInfo;
        }
        System.out.println(user);
        Integer uid = user.getUid();
        List<GoodPicture> list;
        //  查询该用户发布过的，已下架（状态为1）的所有商品以及对应的一张图片
        list = goodService.findAllMyGoodAndPictureByUidAndStatus(uid,1);
        pageInfo = new PageInfo<>(list);
        for (GoodPicture goodPicture:list
                ) {
            System.out.println(goodPicture);
        }
        return pageInfo;
    }

    //  显示我发布并且已售出的商品
    @ResponseBody
    @GetMapping("/queryAllMySailOutGoodAndPicture")
    public PageInfo<GoodPicture> queryAllMySailOutGoodAndPicture(
            @RequestParam(value="pageNo",required=false,defaultValue="1")int pageNo,
            @RequestParam(value="pageSize",required=false,defaultValue="4")int pageSize,
            HttpServletRequest request)
    {   PageInfo<GoodPicture> pageInfo = null;
        if(pageNo<=0){
            pageNo=1;
        }
        PageHelper.startPage(pageNo,pageSize);
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
//            没有登录，跳到登录界面
            return pageInfo;
        }
        System.out.println(user);
        Integer uid = user.getUid();
        List<GoodPicture> list;
        //  查询该用户发布过的，已售出（状态为2）的所有商品以及对应的一张图片
        list = goodService.findAllMyGoodAndPictureByUidAndStatus(uid,2);
        pageInfo = new PageInfo<>(list);
        for (GoodPicture goodPicture:list
                ) {
            System.out.println(goodPicture);
        }
        return pageInfo;
    }


//下架我的商品
    @GetMapping("/soldDownGood/{goodId}")
    public String soldDownGood(
            @PathVariable(value = "goodId") Integer goodId,
            HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if (user == null){
            return "redirect:/hello/login.html";
        }
        Integer uid = user.getUid();
        Good good = goodService.findGoodById(goodId);
//       如果当前用户不是商品的卖家，则重定向到当前用户自己发布了的商品的界面
        if(good.getUserId() != uid){
            return "redirect:/hello/sailGoods";
        }
        good.setStatus(1);
        goodService.soldOutGood(good);
        //      时间记录表插入下架时间
        System.out.println("插入下架时间记录");
        TimeRecord timeRecord = new TimeRecord();
        //      主键一定不能为空，否则不能执行更改语句
        timeRecord.setRecordId(timeRecordService.getRecordIdByGoodId(goodId));
        timeRecord.setGoodId(goodId);
        timeRecord.setDowntime(new Date());
        System.out.println(timeRecord);
        timeRecordService.changeTimeRecord(timeRecord);
//        修改成功，跳到用户发布了的商品的界面
        return "redirect:/hello/sailGoods";

    }

    //将商品的状态设置为已售：2
    @GetMapping("/soldOutGood/{goodId}")
    public String soldOutGood(
            @PathVariable(value = "goodId") Integer goodId,
            HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if (user == null){
            return "redirect:/hello/login.html";
        }
        Integer uid = user.getUid();
        Good good = goodService.findGoodById(goodId);
//       如果当前用户不是商品的卖家，则重定向到当前用户自己发布了的商品的界面
        if(good.getUserId() != uid){
            return "redirect:/hello/sailGoods";
        }
        good.setStatus(2);
        goodService.soldOutGood(good);
        //      时间记录表插入 已售 时间
        System.out.println("插入已售时间记录");
        TimeRecord timeRecord = new TimeRecord();
        //      主键一定不能为空，否则不能执行更改语句
        timeRecord.setRecordId(timeRecordService.getRecordIdByGoodId(goodId));
        timeRecord.setGoodId(goodId);
        timeRecord.setSailtime(new Date());
        System.out.println(timeRecord);
        timeRecordService.changeTimeRecord(timeRecord);
//        修改成功，跳到用户发布了的商品的界面
        return "redirect:/hello/sailGoods";
    }

// 显示要修改的商品的详细信息
    @GetMapping("/toUpdateGood/{goodId}")
    @ResponseBody
    public GoodPicture toUpdateGood(
            @PathVariable(value = "goodId") Integer goodId,
            HttpServletRequest request){
            GoodPicture goodPicture = null;
        User user = (User)request.getSession().getAttribute("user");
    //  防止未登录直接通过超链接进入该方法
        if (user == null){
            return goodPicture;
        }
        Integer uid = user.getUid();
        Good good = goodService.findGoodById(goodId);
    //  如果当前用户不是商品的卖家，则重定向到当前用户自己发布了的商品的界面
        if(good.getUserId() != uid){
            return goodPicture;
        }
    //   根据goodId获取对应的商品信息以及图片
        goodPicture = goodService.findGoodAndPictureByGoodId(goodId);
        return goodPicture;
    }

//    真正的修改当前商品
    @RequestMapping("/updateGood")
    public String updateGood(GoodPicture goodPicture,MultipartFile pic,HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
//  防止未登录直接通过超链接进入该方法
        if (user == null){
            return "redirect:/hello/login.html";
        }
        Integer uid = user.getUid();
//  如果当前用户不是商品的卖家，则重定向到当前用户自己发布了的商品的界面
        /*if(goodPicture.getUserId() != uid){
            return "redirect:/hello/sailGoods";
        }*/
//  如果用户重新上传了图片，则要删除旧图片，并修改
        try {
            if(pic.getSize()>0) {
//                获取存放图片的路径
                String path = System.getProperty("user.dir")+ "\\target\\classes\\static\\goodPicture";
                //获取老图片,并将其删除
                File oldFile = new File(path,goodPicture.getPictureUrl());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                //获取新图片的名称
                String picname=pic.getOriginalFilename();
                //部分浏览器上传照片时将完整路径也上传的情况下，只获取文件最后的名字
                int unixSep = picname.lastIndexOf('/');
                int winSep = picname.lastIndexOf('\\');
                int pos = (winSep > unixSep ? winSep : unixSep);
                if (pos != -1)  {
                    picname = picname.substring(pos + 1);
                }
                String uuid= UUID.randomUUID().toString().replace("-","");
                picname=uuid+"_"+picname;
//              将新图片放入服务器目录中
                File targetFile = new File(path,picname);
                pic.transferTo(targetFile);
//              获取新图片的名称，在后面用来修改picture表
                goodPicture.setPictureUrl(picname);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Good good = new Good();
//      主键一定不能为空，否则不能执行更改语句
        good.setGoodId(goodPicture.getGoodId());
        good.setGoodName(goodPicture.getGoodName());
        good.setPrice(goodPicture.getPrice());
        good.setDescription(goodPicture.getDescription());
        good.setCatelog(goodPicture.getCatelog());
//        good.setUserId(goodPicture.getUserId());
        System.err.println(good);

//      除了主键不能为空外，还至少要有一个字段不是空，否则运行SQL语句是会报错
        Pictures pictures = new Pictures();
        pictures.setPictureId(goodPicture.getPictureId());
        pictures.setPictureUrl(goodPicture.getPictureUrl());
//        pictures.setGoodId(goodPicture.getGoodId());
        System.err.println(pictures);

        goodService.updateGood(good);
        pictureService.updatePicture(pictures);
        return "redirect:/hello/sailGoods";
    }

}
