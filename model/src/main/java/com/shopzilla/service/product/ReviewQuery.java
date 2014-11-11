/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product;

import com.google.common.base.Objects;

/**
 * Query parameters for the review service.
 * @author Chris McAndrews
 */
public class ReviewQuery {

    private Long pid;

    private ReviewQuery() {
        // use the builder
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
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

        ReviewQuery rhs = (ReviewQuery) o;
        return Objects.equal(pid, rhs.pid);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ReviewQuery.class)
                .add("pid", pid)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ReviewQuery q = new ReviewQuery();

        public Builder pid(Long pid) {
            q.pid = pid;
            return this;
        }

        public ReviewQuery build() {
            return q;
        }
    }
}
