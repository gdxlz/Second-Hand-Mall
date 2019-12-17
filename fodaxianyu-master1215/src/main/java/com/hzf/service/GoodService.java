package com.hzf.service;

import com.hzf.entity.Collect;
import com.hzf.entity.Good;
import com.hzf.entity.GoodPicture;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoodService {
    List<GoodPicture> findAllGoodPicture();
    List<GoodPicture> findAllGoodPictureByUserFavotite(Integer uid);
    List<GoodPicture> findAllGoodPictureByString(String str);
    Good findGoodById(Integer goodid);
    void saveGood(Good good);
    Integer findLastGood();
    List<Collect> findGoodIdByUser (Integer uid);
    List<Collect> getAllCollectByUid(int uid);
    List<Collect> getAllCollectByGoodId(int gid);
    void saveCollect(Collect collect);
    void deleteCollection(Integer userId,Integer goodId);
    List<GoodPicture> findAllMyGoodAndPictureByUid(Integer uid);
    List<GoodPicture> findAllMyGoodAndPictureByUidAndStatus(Integer uid,Integer status);

    void soldOutGood(Good good);
    GoodPicture findGoodAndPictureByGoodId(Integer goodId);
    void updateGood(Good good);
}
