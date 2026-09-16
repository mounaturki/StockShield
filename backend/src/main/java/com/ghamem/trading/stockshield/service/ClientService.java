package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.ClientDto;
import com.ghamem.trading.stockshield.entity.Client;
import com.ghamem.trading.stockshield.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final AuditService auditService;

    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream().map(this::toDto).toList();
    }

    public ClientDto findById(Long id) {
        return toDto(clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé")));
    }

    @Transactional
    public ClientDto create(ClientDto dto, String performedBy) {
        Client client = Client.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .manager(dto.getManager())
                .contractPath(dto.getContractPath())
                .contractExpiry(dto.getContractExpiry())
                .build();
        client = clientRepository.save(client);
        auditService.log(null, performedBy, "CREATE_CLIENT", "Client", client.getId(), null, client.getName(), null);
        return toDto(client);
    }

    @Transactional
    public ClientDto update(Long id, ClientDto dto, String performedBy) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        client.setName(dto.getName());
        client.setAddress(dto.getAddress());
        client.setPhone(dto.getPhone());
        client.setManager(dto.getManager());
        client.setContractPath(dto.getContractPath());
        client.setContractExpiry(dto.getContractExpiry());
        client = clientRepository.save(client);
        auditService.log(null, performedBy, "UPDATE_CLIENT", "Client", client.getId(), null, client.getName(), null);
        return toDto(client);
    }

    @Transactional
    public void delete(Long id, String performedBy) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        client.setActive(false);
        clientRepository.save(client);
        auditService.log(null, performedBy, "DELETE_CLIENT", "Client", id, client.getName(), null, null);
    }

    private ClientDto toDto(Client client) {
        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .address(client.getAddress())
                .phone(client.getPhone())
                .manager(client.getManager())
                .contractPath(client.getContractPath())
                .contractExpiry(client.getContractExpiry())
                .active(client.isActive())
                .build();
    }
}
