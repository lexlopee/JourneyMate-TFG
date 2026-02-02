package com.example.JourneyMate.dao.payment;

import com.example.JourneyMate.entity.payment.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PagoRepository extends JpaRepository<PagoEntity, Integer> {

    List<PagoEntity> findByReserva_IdReserva(Integer idReserva);

    List<PagoEntity> findByIdPago(Integer idPago);

    PagoEntity findByIdPagoAndReserva_IdReserva(Integer idPago, Integer idReserva);

    @Query("SELECT p FROM PagoEntity p WHERE TYPE(p.reserva.servicio) = HotelEntity AND p.reserva.servicio.idServicio = :idHotel")
    List<PagoEntity> findPagosByHotel(Integer idHotel);

    @Query("SELECT p FROM PagoEntity p WHERE TYPE(p.reserva.servicio) = HotelEntity AND p.reserva.servicio.idServicio = :idHotel AND p.reserva.idReserva = :idReserva AND p.reserva.servicio.idServicio = :idServicio")
    List<PagoEntity> findPagosHotelCompleto(Integer idHotel, Integer idReserva, Integer idServicio);

    @Query("DELETE FROM PagoEntity p WHERE TYPE(p.reserva.servicio) = HotelEntity AND p.reserva.servicio.idServicio = :idHotel")
    void deletePagosByHotel(Integer idHotel);

}
