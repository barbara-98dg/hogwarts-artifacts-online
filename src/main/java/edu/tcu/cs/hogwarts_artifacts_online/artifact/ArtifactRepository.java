package edu.tcu.cs.hogwarts_artifacts_online.artifact;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtifactRepository extends JpaRepository<Artifact, String> {
}
