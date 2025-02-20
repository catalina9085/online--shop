package com.online.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.shop.entities.Shipment;

@Repository
public interface ShipmentRepo extends JpaRepository<Shipment,Long>{

}
