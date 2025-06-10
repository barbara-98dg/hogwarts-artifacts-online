package edu.tcu.cs.hogwarts_artifacts_online.wizard;

import edu.tcu.cs.hogwarts_artifacts_online.artifact.Artifact;
import edu.tcu.cs.hogwarts_artifacts_online.artifact.ArtifactRepository;
import edu.tcu.cs.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards;

    @BeforeEach
    void setUp(){
        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");

        this.wizards = new ArrayList<>();
        this.wizards.add(w1);
        this.wizards.add(w2);
        this.wizards.add(w3);
    }

    @Test
    void testFindByIdSuccess(){
        // Given
        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        given(wizardRepository.findById(2)).willReturn(Optional.of(w));

        //When
        Wizard returnedWizard = wizardService.findById(2);

        //Then
        assertThat(returnedWizard.getId()).isEqualTo(w.getId());
        assertThat(returnedWizard.getName()).isEqualTo(w.getName());
        assertThat(returnedWizard.getNumberOfArtifacts()).isEqualTo(w.getNumberOfArtifacts());

        verify(wizardRepository, times(1)).findById(2);
    }

    @Test
    void testFindByIdNotFound(){
        // Given
        given(wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        //When
        Throwable thrown = catchThrowable(()->{
            Wizard returnedWizard = wizardService.findById(2);
        });

        //Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class).hasMessage("Could not find wizard with Id 2 :(");
        verify(wizardRepository, times(1)).findById(2);
    }

    @Test
    void testFindAllSuccess(){
        // Given
        given(wizardRepository.findAll()).willReturn(this.wizards);

        //When
        List<Wizard> actualWizards = wizardService.findAll();

        //Then
        assertThat(actualWizards.size()).isEqualTo(wizards.size());
        verify(wizardRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess(){
        // Given
        Wizard newWizard = new Wizard();
        newWizard.setId(5);
        newWizard.setName("Harry Potter");

        given(wizardRepository.save(newWizard)).willReturn(newWizard);

        //When
        Wizard savedWizard = wizardService.save(newWizard);

        //Then
        assertThat(savedWizard.getId()).isEqualTo(5);
        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());
        assertThat(savedWizard.getNumberOfArtifacts()).isEqualTo(newWizard.getNumberOfArtifacts());
        verify(wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testUpdateSuccess(){
        // Given
        Wizard oldWizard = new Wizard();
        oldWizard.setId(5);
        oldWizard.setName("Harry Potter");

        Wizard update = new Wizard();
        update.setId(5);
        update.setName("Harry new");

        given(wizardRepository.findById(5)).willReturn(Optional.of(oldWizard));
        given(wizardRepository.save(oldWizard)).willReturn(oldWizard);

        //When
        Wizard updatedWizard = wizardService.update(5, update);

        //Then
        assertThat(updatedWizard.getId()).isEqualTo(update.getId());
        assertThat(updatedWizard.getName()).isEqualTo(update.getName());
        assertThat(updatedWizard.getNumberOfArtifacts()).isEqualTo(update.getNumberOfArtifacts());
        verify(wizardRepository, times(1)).findById(5);
        verify(wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateNotFound(){
        // Given
        Wizard update = new Wizard();
        update.setId(5);
        update.setName("Harry new");

        given(wizardRepository.findById(5)).willReturn(Optional.empty());
        //When
        assertThrows(ObjectNotFoundException.class, ()->{
            wizardService.update(5, update);
        });

        //Then
        verify(wizardRepository, times(1)).findById(5);
    }

    @Test
    void testDeleteSuccess(){
        // Given
        Wizard wizard = new Wizard();
        wizard.setId(5);
        wizard.setName("Harry Potter");

        Artifact artifact1 = new Artifact();
        artifact1.setId("a1");
        artifact1.setName("Wand");
        artifact1.setOwner(wizard);

        Artifact artifact2 = new Artifact();
        artifact2.setId("a2");
        artifact2.setName("Cloak");
        artifact2.setOwner(wizard);

        List<Artifact> artifacts = List.of(artifact1, artifact2);
        wizard.setArtifacts(artifacts);

        given(wizardRepository.findById(5)).willReturn(Optional.of(wizard));
        //doNothing().when(wizardRepository).deleteById(5);

        // When
        wizardService.delete(5);

        // Then
        assertNull(artifact1.getOwner());
        assertNull(artifact2.getOwner());

        //verify(artifactRepository).saveAll(artifacts);
        verify(wizardRepository, times(1)).deleteById(5);
    }

    @Test
    void testDeleteNotFound(){
        // Given
        given(wizardRepository.findById(5)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, ()->{
            wizardService.delete(5);
        });

        // Then
        verify(wizardRepository, times(1)).findById(5);
    }

    @Test
    void testAssignArtifactToWizardSuccess() {
        // Given
        Wizard oldOwner = new Wizard();
        oldOwner.setId(1);
        oldOwner.setName("Albus Dumbledore");

        Wizard newOwner = new Wizard();
        newOwner.setId(2);
        newOwner.setName("Severus Snape");

        Artifact artifact = new Artifact();
        artifact.setId("a1");
        artifact.setName("Elder Wand");
        artifact.setDescription("Description");
        artifact.setImageUrl("ImageUrl");

        oldOwner.addArtifact(artifact);

        // Mock repository behaviors
        given(wizardRepository.findById(2)).willReturn(Optional.of(newOwner));
        given(artifactRepository.findById("a1")).willReturn(Optional.of(artifact));

        // When
        wizardService.assignArtifactToWizard(2, "a1");

        // Then
        assertEquals(2, artifact.getOwner().getId()); // nuovo owner assegnato
        assertTrue(newOwner.getArtifacts().contains(artifact)); // artefatto presente nel nuovo wizard
        assertFalse(oldOwner.getArtifacts().contains(artifact)); // artefatto rimosso dal vecchio wizard
    }

    @Test
    void testAssignArtifactToWizardErrorWithNonExistentWizardId() {
        // Given
        Wizard w = new Wizard();
        w.setId(1);
        w.setName("Albus Dumbledore");

        Artifact artifact = new Artifact();
        artifact.setId("a1");
        artifact.setName("Elder Wand");
        artifact.setDescription("Description");
        artifact.setImageUrl("ImageUrl");

        w.addArtifact(artifact);

        // Mock repository behaviors
        given(wizardRepository.findById(2)).willReturn(Optional.empty());
        given(artifactRepository.findById("a1")).willReturn(Optional.of(artifact));

        // When
        Throwable thrown = assertThrows(ObjectNotFoundException.class, ()->{
            this.wizardService.assignArtifactToWizard(2,"a1");
        });

        // Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                        .hasMessage("Could not find wizard with Id 2 :(");
        assertEquals(1, artifact.getOwner().getId());
    }

    @Test
    void testAssignArtifactToWizardErrorWithNonExistentArtifactId() {
        // Given

        // Mock repository behaviors
        given(artifactRepository.findById("a1")).willReturn(Optional.empty());

        // When
        Throwable thrown = assertThrows(ObjectNotFoundException.class, ()->{
            this.wizardService.assignArtifactToWizard(2,"a1");
        });

        // Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id a1 :(");
    }


}
