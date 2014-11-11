package com.shopzilla.service.product.data;

import com.shopzilla.service.product.ProductQuery;
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
@RegisterMapper(ProductEntry.ProductEntryMapper.class)
public interface ProductDao extends Transactional<ProductDao> {
    /* Create */
    @SqlUpdate(
            " INSERT INTO product_entry (product_id, product_category, product_name) " + " VALUES (:q.productId, :q.productCategory, :q.productName)"
    )
    public void createProductEntry(@BindBean("q") ProductEntry product);

    /* Read */
    @SqlQuery(
            "SELECT * FROM product_entry WHERE product_id = :q.productId "
    )
    public List<ProductEntry> getProductEntries(@BindBean("q") ProductQuery query);

    /* Update */
    @SqlUpdate(
            " UPDATE product_entry " +
                    " SET product_name = :q.productName " +
                    " WHERE product_id = :q.productId "
    )
    public int updateProductEntry(@BindBean("q") ProductEntry product);

    /* Delete */
    @SqlUpdate(
            "DELETE from product_entry WHERE product_id = :q.productId "
    )
    public int deleteProductEntry(@BindBean("q") ProductQuery query);
}
