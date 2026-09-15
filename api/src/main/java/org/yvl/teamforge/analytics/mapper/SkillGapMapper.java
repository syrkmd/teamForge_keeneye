package org.yvl.teamforge.analytics.mapper;

import org.mapstruct.Mapper;
import org.yvl.teamforge.analytics.dto.projection.SkillGapRow;
import org.yvl.teamforge.analytics.dto.response.SkillGapView;

@Mapper(componentModel = "spring")
public interface SkillGapMapper {

    SkillGapView toSkillGapView(SkillGapRow skillGapRow);
}
