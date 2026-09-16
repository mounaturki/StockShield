package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.MachineDto;
import com.ghamem.trading.stockshield.entity.Client;
import com.ghamem.trading.stockshield.entity.Machine;
import com.ghamem.trading.stockshield.repository.ClientRepository;
import com.ghamem.trading.stockshield.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;
    private final ClientRepository clientRepository;
    private final AuditService auditService;

    public List<MachineDto> findAll() {
        return machineRepository.findAll().stream().map(this::toDto).toList();
    }

    public MachineDto findById(Long id) {
        return toDto(machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine non trouvée")));
    }

    @Transactional
    public MachineDto create(MachineDto dto, String performedBy) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Machine machine = Machine.builder()
                .serialNumber(dto.getSerialNumber())
                .model(dto.getModel())
                .machineType(dto.getMachineType())
                .client(client)
                .status(dto.getStatus())
                .installationDate(dto.getInstallationDate())
                .lastMaintenance(dto.getLastMaintenance())
                .nextMaintenance(dto.getNextMaintenance())
                .photoPath(dto.getPhotoPath())
                .notes(dto.getNotes())
                .build();
        machine = machineRepository.save(machine);
        auditService.log(null, performedBy, "CREATE_MACHINE", "Machine", machine.getId(), null, machine.getSerialNumber(), null);
        return toDto(machine);
    }

    @Transactional
    public MachineDto update(Long id, MachineDto dto, String performedBy) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine non trouvée"));
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            machine.setClient(client);
        }
        machine.setSerialNumber(dto.getSerialNumber());
        machine.setModel(dto.getModel());
        if (dto.getMachineType() != null) {
            machine.setMachineType(dto.getMachineType());
        }
        machine.setStatus(dto.getStatus());
        machine.setInstallationDate(dto.getInstallationDate());
        machine.setLastMaintenance(dto.getLastMaintenance());
        machine.setNextMaintenance(dto.getNextMaintenance());
        machine.setPhotoPath(dto.getPhotoPath());
        machine.setNotes(dto.getNotes());
        machine = machineRepository.save(machine);
        auditService.log(null, performedBy, "UPDATE_MACHINE", "Machine", machine.getId(), null, machine.getSerialNumber(), null);
        return toDto(machine);
    }

    @Transactional
    public void delete(Long id, String performedBy) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine non trouvée"));
        machineRepository.delete(machine);
        auditService.log(null, performedBy, "DELETE_MACHINE", "Machine", id, machine.getSerialNumber(), null, null);
    }

    private MachineDto toDto(Machine machine) {
        return MachineDto.builder()
                .id(machine.getId())
                .serialNumber(machine.getSerialNumber())
                .model(machine.getModel())
                .machineType(machine.getMachineType())
                .machineTypeLabel(machine.getMachineType() != null ? machine.getMachineType().getLabel() : null)
                .clientId(machine.getClient() != null ? machine.getClient().getId() : null)
                .clientName(machine.getClient() != null ? machine.getClient().getName() : null)
                .status(machine.getStatus())
                .installationDate(machine.getInstallationDate())
                .lastMaintenance(machine.getLastMaintenance())
                .nextMaintenance(machine.getNextMaintenance())
                .photoPath(machine.getPhotoPath())
                .notes(machine.getNotes())
                .build();
    }
}
