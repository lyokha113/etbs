package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateCreateResponse;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.RawTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RawTemplateServiceImpl implements RawTemplateService {
    @Autowired
    RawTemplateRepository rawTemplateRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;

    @Override
    public RawTemplateCreateResponse createTemplate(RawTemplateCreateRequest request) {
        RawTemplate rawTemplate = new RawTemplate();
        rawTemplate.setContent(request.getContent());
        rawTemplate.setDescription(request.getDescription());
        rawTemplate.setName(request.getName());
        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId()).get();
        rawTemplate.setWorkspace(workspace);
        rawTemplate.setActive(true);
        rawTemplateRepository.save(rawTemplate);

        RawTemplateCreateResponse response = new RawTemplateCreateResponse();
        response.setId(rawTemplate.getId());
        return response;
    }
}
