package com.progastro.inventario.services.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.mappers.RolMapper;
import com.progastro.inventario.models.DTO.RolResponseDTO;
import com.progastro.inventario.models.Entities.Rol;
import com.progastro.inventario.repositories.RolRepository;
import com.progastro.inventario.services.RolServiceBridge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolServiceBridge{

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RolResponseDTO> listarRoles() {
        
        List<Rol> roles = rolRepository.findAll();

        return roles.stream().map(rolMapper::toResponse).toList();
    }
    
    
}
