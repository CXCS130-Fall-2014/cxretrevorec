package com.shopzilla.service.product.data;

import com.shopzilla.service.product.CategoryQuery;
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
@RegisterMapper(CategoryEntry.CategoryEntryMapper.class)
public interface CategoryDao extends Transactional<CategoryDao> {
    /* Get Categories */
    @SqlQuery(
            "SELECT DISTINCT product_category FROM product_entry "
    )
    public List<CategoryEntry> getCategoryList(@BindBean("q") CategoryQuery query);

}
