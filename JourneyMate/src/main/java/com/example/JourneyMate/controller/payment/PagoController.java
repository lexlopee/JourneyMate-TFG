package com.example.JourneyMate.controller.payment;

import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.payment.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<List<PagoEntity>> findByReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(pagoService.findByReservaIdReserva(idReserva));
    }

    @GetMapping("/id/{idPago}")
    public ResponseEntity<List<PagoEntity>> findById(@PathVariable Integer idPago) {
        return ResponseEntity.ok(pagoService.findById(idPago));
    }

    @GetMapping("/{idPago}/reserva/{idReserva}")
    public ResponseEntity<PagoEntity> findByIdAndReserva(
            @PathVariable Integer idPago,
            @PathVariable Integer idReserva) {

        PagoEntity pago = pagoService.findById(idPago, idReserva);
        return pago != null ? ResponseEntity.ok(pago) : ResponseEntity.notFound().build();
    }

    @GetMapping("/hotel/{idHotel}")
    public ResponseEntity<List<PagoEntity>> findByHotel(@PathVariable Integer idHotel) {
        return ResponseEntity.ok(pagoService.findByHotelId(idHotel));
    }

    @GetMapping("/hotel/{idHotel}/reserva/{idReserva}/servicio/{idServicio}")
    public ResponseEntity<List<PagoEntity>> findByHotelReservaServicio(
            @PathVariable Integer idHotel,
            @PathVariable Integer idReserva,
            @PathVariable Integer idServicio) {

        return ResponseEntity.ok(
                pagoService.findByHotelId(idHotel, idReserva, idServicio)
        );
    }

    @PostMapping
    public ResponseEntity<PagoEntity> save(@RequestBody PagoEntity pago) {
        return ResponseEntity.ok(pagoService.save(pago));
    }

    @DeleteMapping("/hotel/{idHotel}")
    public ResponseEntity<Void> deleteByHotel(@PathVariable Integer idHotel) {
        pagoService.deleteByHotelId(idHotel);
        return ResponseEntity.noContent().build();
    }
}
