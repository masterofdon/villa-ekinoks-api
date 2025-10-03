package com.villaekinoks.app.servicableitem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.villaekinoks.app.servicableitem.ServicableItem;
import com.villaekinoks.app.servicableitem.repository.ServicableItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicableItemService {

  private final ServicableItemRepository servicableItemRepository;

  public ServicableItem getById(String id) {
    return this.servicableItemRepository.findById(id).orElse(null);
  }

  public Page<ServicableItem> getAll(Pageable pageable) {
    return this.servicableItemRepository.findAll(pageable);
  }

  public ServicableItem create(ServicableItem item) {
    return this.servicableItemRepository.save(item);
  }

  public void delete(ServicableItem item) {
    this.servicableItemRepository.delete(item);
  }
}
