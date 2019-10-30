package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.Workspace;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateCreateResponse;
import fpt.capstone.etbs.payload.RawTemplateResponse;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.RawTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<RawTemplateResponse> getTemplateByWorkspaceId(int wId) {
        List<RawTemplate> rawTemplate = rawTemplateRepository.getAllByWorkspace_Id(wId);
        List<RawTemplateResponse> response = new ArrayList<>();
        for (int i = 0; i < rawTemplate.size(); i++) {
            RawTemplateResponse temp = new RawTemplateResponse();
            temp.setId(rawTemplate.get(i).getId());
            temp.setName(rawTemplate.get(i).getName());
            temp.setWorkspaceId(wId);
            temp.setContent(rawTemplate.get(i).getContent());
            temp.setDescription(rawTemplate.get(i).getDescription());
            temp.setThumbnail(rawTemplate.get(i).getThumbnail());

            response.add(temp);
        }

        return response;
    }

    @Override
    public RawTemplate getRawTemplateById(int id) {
        return rawTemplateRepository.findById(id).orElse(null);
    }
}
