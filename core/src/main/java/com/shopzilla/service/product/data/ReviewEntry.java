/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product.data;

import com.google.common.base.Objects;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database model that represents a review.
 * @author Chris McAndrews
 */
public class ReviewEntry {

    private Long pid;
    private Long rid;
    private String title;
    private int rating;
    private String comment;

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }
    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Map database rows to the {@link com.shopzilla.service.product.data.ReviewEntry} class.
     */
    public static class ReviewEntryMapper implements ResultSetMapper<ReviewEntry> {
        @Override
        public ReviewEntry map(int index, ResultSet rs, StatementContext stx) throws SQLException {
            final ReviewEntry toReturn = new ReviewEntry();
            toReturn.setPid(rs.getLong("pid"));
            toReturn.setRid(rs.getLong("rid"));
            toReturn.setTitle(rs.getString("title"));
            toReturn.setRating(rs.getInt("rating"));
            toReturn.setComment(rs.getString("comment"));
            return toReturn;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != getClass()) {
            return false;
        }

        ReviewEntry rhs = (ReviewEntry) o;
        return Objects.equal(pid, rhs.pid);
    }


}
