package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Notificacion;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.repositories.NotificacionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<Notificacion> listarTodas() {
        return notificacionRepository.findAll();
    }

    public Optional<Notificacion> obtener(Long id) {
        return notificacionRepository.findById(id);
    }

    public Notificacion guardar(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    public void eliminar(Long id) {
        notificacionRepository.deleteById(id);
    }

    public List<Notificacion> obtenerPorUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuario(usuario);
    }

    public void marcarComoLeida(Long id) {
        Optional<Notificacion> opt = notificacionRepository.findById(id);
        if (opt.isPresent()) {
            Notificacion n = opt.get();
            n.setLeida(true);
            notificacionRepository.save(n);
        }
    }
}

