package com.hzf.repository;

import com.hzf.entity.GoodPicture;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodPictureMapper {
//    查找所有的商品以及图片信息
    List<GoodPicture> findAllGoodPicture();
    List<GoodPicture> findAllGoodPictureByUserFavotite(Integer uid);
    List<GoodPicture> findAllGoodPictureByString(String str);
    List<GoodPicture> findAllMyGoodAndPictureByUid(Integer uid);
//    根据用户名和其发布的商品的状态来查找商品和图片
//    因为考虑到OCP原则，不对上面的代码进行修改
    List<GoodPicture> findAllMyGoodAndPictureByUidAndStatus(@Param("uid")Integer uid,@Param("status") Integer status);

    GoodPicture findGoodAndPictureByGoodId(Integer goodId);
}
