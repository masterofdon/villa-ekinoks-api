package com.villaekinoks.app.villa.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.villaekinoks.app.villa.Villa;

@Component
public class VillaSpecification {

  public Specification<Villa> query(String _query) {
    return (root, query, cb) -> cb.or(
        cb.like(root.get("publicinfo").get("name"), "%" + _query + "%"),
        cb.like(root.get("publicinfo").get("slug"), "%" + _query + "%"),
        cb.like(root.get("publicinfo").get("description"), "%" + _query + "%"));
  }

  public Specification<Villa> conditionalSearch(
      String query,
      String[] ids) {
    Specification<Villa> spec = Specification.not(null);
    if (query != null) {
      spec = spec.and(query(query));
    }

    return spec;
  }
}
