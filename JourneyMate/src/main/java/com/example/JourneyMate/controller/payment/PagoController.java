package com.example.JourneyMate.controller.payment;

import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.payment.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador encargado de gestionar operaciones relacionadas
 * con pagos dentro del sistema JourneyMate.
 * <p>
 * Permite consultar, registrar y eliminar pagos asociados
 * a reservas y servicios.
 */
@RestController
@RequestMapping("/pagos")
public class PagoController {

    /**
     * Servicio encargado de la lógica de negocio de pagos.
     */
    @Autowired
    private PagoService pagoService;

    /**
     * Obtiene todos los pagos asociados a una reserva.
     *
     * @param idReserva identificador de la reserva
     * @return lista de pagos encontrados
     */
    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<List<PagoEntity>> findByReserva(
            @PathVariable Integer idReserva) {

        return ResponseEntity.ok(
                pagoService.findByReservaIdReserva(idReserva)
        );
    }

    /**
     * Busca pagos por su identificador.
     *
     * @param idPago identificador del pago
     * @return lista de pagos encontrados
     */
    @GetMapping("/id/{idPago}")
    public ResponseEntity<List<PagoEntity>> findById(
            @PathVariable Integer idPago) {

        return ResponseEntity.ok(
                pagoService.findById(idPago)
        );
    }

    /**
     * Busca un pago específico asociado a una reserva.
     *
     * @param idPago identificador del pago
     * @param idReserva identificador de la reserva
     * @return pago encontrado o respuesta 404 si no existe
     */
    @GetMapping("/{idPago}/reserva/{idReserva}")
    public ResponseEntity<PagoEntity> findByIdAndReserva(
            @PathVariable Integer idPago,
            @PathVariable Integer idReserva) {

        PagoEntity pago =
                pagoService.findById(idPago, idReserva);

        return pago != null
                ? ResponseEntity.ok(pago)
                : ResponseEntity.notFound().build();
    }

    /**
     * Obtiene todos los pagos asociados a un hotel.
     *
     * @param idHotel identificador del hotel
     * @return lista de pagos encontrados
     */
    @GetMapping("/hotel/{idHotel}")
    public ResponseEntity<List<PagoEntity>> findByHotel(
            @PathVariable Integer idHotel) {

        return ResponseEntity.ok(
                pagoService.findByHotelId(idHotel)
        );
    }

    /**
     * Busca pagos filtrando por hotel, reserva y servicio.
     *
     * @param idHotel identificador del hotel
     * @param idReserva identificador de la reserva
     * @param idServicio identificador del servicio
     * @return lista de pagos encontrados
     */
    @GetMapping("/hotel/{idHotel}/reserva/{idReserva}/servicio/{idServicio}")
    public ResponseEntity<List<PagoEntity>> findByHotelReservaServicio(
            @PathVariable Integer idHotel,
            @PathVariable Integer idReserva,
            @PathVariable Integer idServicio) {

        return ResponseEntity.ok(
                pagoService.findByHotelId(
                        idHotel,
                        idReserva,
                        idServicio
                )
        );
    }

    /**
     * Registra un nuevo pago en el sistema.
     *
     * @param pago información del pago
     * @return pago registrado
     */
    @PostMapping
    public ResponseEntity<PagoEntity> save(
            @RequestBody PagoEntity pago) {

        return ResponseEntity.ok(
                pagoService.save(pago)
        );
    }

    /**
     * Elimina todos los pagos asociados a un hotel.
     *
     * @param idHotel identificador del hotel
     * @return respuesta vacía si la eliminación fue exitosa
     */
    @DeleteMapping("/hotel/{idHotel}")
    public ResponseEntity<Void> deleteByHotel(
            @PathVariable Integer idHotel) {

        pagoService.deleteByHotelId(idHotel);

        return ResponseEntity.noContent().build();
    }
}