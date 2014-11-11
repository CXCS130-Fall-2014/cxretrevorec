package com.shopzilla.service.product.data;

import com.shopzilla.service.product.ReviewQuery;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import java.util.List;

/**
 * @author Rachel Baldovino <rbaldovino@shopzilla.com>
 * @created 1/22/14
 */
@RegisterMapper(ReviewEntry.ReviewEntryMapper.class)
public interface ReviewDao extends Transactional<ReviewDao> {
    /* Read */
    @SqlQuery(
            "SELECT * FROM reviews WHERE pid = :q.pid "
    )
    public List<ReviewEntry> getReviewEntries(@BindBean("q") ReviewQuery query);

}
