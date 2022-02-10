package com.game.witticism.repository;

import com.game.witticism.model.Game;
import com.game.witticism.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByCode(String code);
}
