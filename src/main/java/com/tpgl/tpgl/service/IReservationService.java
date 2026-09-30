package com.tpgl.tpgl.service;

import com.tpgl.tpgl.web.dto.ReservationRequestDTO;
import com.tpgl.tpgl.web.dto.ReservationResponseDTO;

import java.util.List;

/**
 * Interface du service métier de gestion des réservations AutoLoc (Séances 8, 9 & 11).
 */
public interface IReservationService {

    ReservationResponseDTO creerReservation(ReservationRequestDTO request);

    ReservationResponseDTO validerReservation(Long reservationId);

    ReservationResponseDTO annulerReservation(Long reservationId);

    ReservationResponseDTO terminerReservation(Long reservationId);

    ReservationResponseDTO getReservationById(Long id);

    List<ReservationResponseDTO> getAllReservations();

    List<ReservationResponseDTO> getReservationsByClient(Long clientId);
}
