package com.example.JourneyMate.dao.payment;

import com.example.JourneyMate.entity.payment.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//Arreglar esta clase
public interface PagoRepository extends JpaRepository<PagoEntity, Integer> {
    List<PagoEntity> findByReservaIdReserva(Integer idReserva);

    List<PagoEntity> findByIdPago(Integer idPago);

    PagoEntity findByIdPagoAndReserva_IdReserva(Integer idPago, Integer idReserva);

    List<PagoEntity> findByReserva_Hotel_IdHotel(Integer idHotel);

    List<PagoEntity> findByReserva_Hotel_IdHotelAndReserva_IdReservaAndReserva_Servicio_IdServicio(Integer idHotel, Integer idReserva, Integer idServicio);

    void deleteByReserva_Hotel_IdHotel(Integer idHotel);
}
