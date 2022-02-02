package com.game.witticism.repository;

import com.game.witticism.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByName(String name);
    Player findByNameAndGameId(String name, Long gameId);
}
