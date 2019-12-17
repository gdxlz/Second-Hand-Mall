package com.hzf.service.impl;

import com.hzf.entity.*;
import com.hzf.repository.CollectMapper;
import com.hzf.repository.GoodMapper;
import com.hzf.repository.GoodPictureMapper;
import com.hzf.repository.PicturesMapper;
import com.hzf.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodServiceImpl implements GoodService{
    @Autowired
    private GoodMapper goodMapper;
    @Autowired
    private GoodPictureMapper goodPictureMapper;
    @Autowired
    private CollectMapper collectMapper;
//    返回所有的商品以及其对应的一张图片
    @Override
    public List<GoodPicture> findAllGoodPicture() {
        List<GoodPicture> list = goodPictureMapper.findAllGoodPicture();
        return list;
    }

//    返回与用户的收藏商品相同类别的商品
    @Override
    public List<GoodPicture> findAllGoodPictureByUserFavotite(Integer uid) {
        List<GoodPicture> list = goodPictureMapper.findAllGoodPictureByUserFavotite(uid);
        return list;
    }

//    通过输入的字符串对商品进行模糊查询
    @Override
    public List<GoodPicture> findAllGoodPictureByString(String str) {
        List<GoodPicture> list = goodPictureMapper.findAllGoodPictureByString(str);
        return list;
    }

    //  通过商品id查找商品
    @Override
    public Good findGoodById(Integer goodid) {
        Good good =goodMapper.selectByPrimaryKey(goodid);
        return good;
    }

//插入商品
    @Override
    public void saveGood(Good good) {
        goodMapper.insert(good);
    }

// 查找最新的商品的id
    @Override
    public Integer findLastGood() {
        GoodExample example = new GoodExample();
        GoodExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("good_id desc");
        List<Good> list = goodMapper.selectByExample(example) ;
        return list.get(0).getGoodId();
    }

//   通过商品id获取收藏列表（好像没有用，暂且保留）
    @Override
    public List<Collect> getAllCollectByGoodId(int gid) {
        CollectExample example=new CollectExample();
        CollectExample.Criteria criteria=example.createCriteria();
        criteria.andGoodIdEqualTo(gid);
        List<Collect> collectList=collectMapper.selectByExample(example);
        return collectList;
    }

//  获取用户的收藏列表
    @Override
    public List<Collect> getAllCollectByUid(int uid) {
        CollectExample example=new CollectExample();
        CollectExample.Criteria criteria=example.createCriteria();
        criteria.andUserIdEqualTo(uid);
        List<Collect> collectList=collectMapper.selectByExample(example);
        return collectList;
    }

//    增加收藏记录
    @Override
    public void saveCollect(Collect collect) {
        collectMapper.insert(collect);
    }

    //  根据用户id查找该用户的收藏记录
    @Override
    public List<Collect> findGoodIdByUser(Integer uid) {
        CollectExample example = new CollectExample();
        CollectExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(uid);
        return collectMapper.selectByExample(example);
    }

//    删除既含有userId，又含有goodId的collect记录
    @Override
    public void deleteCollection(Integer userId, Integer goodId) {
        CollectExample example = new CollectExample();
        CollectExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andGoodIdEqualTo(goodId);
        collectMapper.deleteByExample(example);
    }

//    查询该用户发布过的还在售所有商品以及对应的一张图片
    @Override
    public List<GoodPicture> findAllMyGoodAndPictureByUid(Integer uid) {
        List<GoodPicture> list = goodPictureMapper.findAllMyGoodAndPictureByUid(uid);
        return list;
    }
//    查询该用户发布过的不同状态的商品以及对应的一张图片
    @Override
    public List<GoodPicture> findAllMyGoodAndPictureByUidAndStatus(Integer uid,Integer status) {
        List<GoodPicture> list = goodPictureMapper.findAllMyGoodAndPictureByUidAndStatus(uid,status);
        return list;
    }

//    用新的good替换旧的good
    @Override
    public void soldOutGood(Good good) {
        goodMapper.updateByPrimaryKey(good);
    }

//根据goodId获取商品的详细信息，用于返回到修改界面
    @Override
    public GoodPicture findGoodAndPictureByGoodId(Integer goodId) {
        GoodPicture goodPicture = goodPictureMapper.findGoodAndPictureByGoodId(goodId);
        return goodPicture;
    }

    @Override
    public void updateGood(Good good) {
//        按主键更新值不为null的字段
        goodMapper.updateByPrimaryKeySelective(good);
    }
}
