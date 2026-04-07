package me.TyAlternative.com.nocturne.core.phase.impl;

import me.TyAlternative.com.nocturne.api.phase.GamePhase;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class EndPhase implements GamePhase {
    @Override
    public void onStart(@NotNull PhaseContext context) {

    }

    @Override
    public void onEnd(@NotNull PhaseContext context) {

    }

    @Override
    public @NotNull PhaseType getType() {
        return null;
    }

    @Override
    public long getDurationMs(@NotNull PhaseContext context) {
        return 0;
    }
}
