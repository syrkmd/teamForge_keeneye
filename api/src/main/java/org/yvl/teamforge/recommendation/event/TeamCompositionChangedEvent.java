package org.yvl.teamforge.recommendation.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamCompositionChangedEvent {

    private final Long teamId;
}
