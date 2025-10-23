package org.fichaje.config.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.UsuarioPrincipal;
import org.fichaje.service.UsuarioService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String numero) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByNumero(numero).get();
        return UsuarioPrincipal.build(usuario);
    }
    
}
