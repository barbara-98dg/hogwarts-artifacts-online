package edu.tcu.cs.hogwarts_artifacts_online.wizard;

import edu.tcu.cs.hogwarts_artifacts_online.artifact.Artifact;
import edu.tcu.cs.hogwarts_artifacts_online.artifact.ArtifactNotFoundException;
import edu.tcu.cs.hogwarts_artifacts_online.artifact.ArtifactRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;

    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public Wizard findById(Integer wizardId){
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(()->new WizardNotFoundException(wizardId));
    }

    public List<Wizard> findAll(){
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard){
        return this.wizardRepository.save(newWizard);
    }

    public Wizard update(Integer wizardId, Wizard update){
        return this.wizardRepository.findById(wizardId)
                .map(oldWizard ->{
                    oldWizard.setName(update.getName());
                    return this.wizardRepository.save(oldWizard);
                })
                .orElseThrow(()->new WizardNotFoundException(wizardId));
    }

    public void delete(Integer wizardId){
        Wizard wizard = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new WizardNotFoundException(wizardId));
        List<Artifact> artifacts = wizard.getArtifacts();
        artifacts.forEach(a -> a.setOwner(null));
        artifactRepository.saveAll(artifacts);
        this.wizardRepository.deleteById(wizardId);
    }

    public void assignArtifactToWizard(Integer wizardId, String artifactId){
        Wizard wizard = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new WizardNotFoundException(wizardId));
        Artifact artifact = this.artifactRepository.findById(artifactId)
                .orElseThrow(()-> new ArtifactNotFoundException(artifactId));
        if(artifact.getOwner() != null){
            Wizard oldOwner = artifact.getOwner();
            oldOwner.removeArtifact(artifact);
            this.wizardRepository.save(oldOwner);
        }
        wizard.addArtifact(artifact);
        this.wizardRepository.save(wizard);
        this.artifactRepository.save(artifact);
    }
}
