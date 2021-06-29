package ifa.devlog.tutorat.dao;

import ifa.devlog.tutorat.model.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NiveauDao extends JpaRepository<Niveau,Integer> {
}
