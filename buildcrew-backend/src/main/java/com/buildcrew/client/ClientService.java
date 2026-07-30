package com.buildcrew.client;

import com.buildcrew.common.dto.PageResponse;
import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    @Inject
    TenantContext tenantContext;

    public PageResponse<ClientDTO> search(String query, int page, int size) {
        UUID companyId = tenantContext.getCompanyId();

        List<ClientDTO> items = clientRepository.search(companyId, query, page, size)
                .stream().map(ClientDTO::from).toList();

        long total = clientRepository.countSearch(companyId, query);
        return new PageResponse<>(items, page, size, total);
    }

    public ClientDTO findById(UUID id) {
        return ClientDTO.from(find(id));
    }

    @Transactional
    public ClientDTO create(ClientCreateDTO dto) {
        Client client = new Client();
        client.id = UUID.randomUUID();
        client.companyId = tenantContext.getCompanyId();
        client.companyName = dto.companyName;
        client.contactPerson = dto.contactPerson;
        client.phone = dto.phone;
        client.email = dto.email;
        client.address = dto.address;
        client.createdAt = OffsetDateTime.now();

        clientRepository.persist(client);
        return ClientDTO.from(client);
    }

    @Transactional
    public ClientDTO update(UUID id, ClientCreateDTO dto) {
        Client client = find(id);
        client.companyName = dto.companyName;
        client.contactPerson = dto.contactPerson;
        client.phone = dto.phone;
        client.email = dto.email;
        client.address = dto.address;
        return ClientDTO.from(client);
    }

    @Transactional
    public void delete(UUID id) {
        Client client = find(id);
        clientRepository.delete(client);
    }

    private Client find(UUID id) {
        Client client = clientRepository.findById(id);
        if (client == null || !client.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("Client not found");
        }
        return client;
    }
}
