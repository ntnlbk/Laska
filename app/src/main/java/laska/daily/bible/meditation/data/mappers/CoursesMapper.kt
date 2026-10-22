package laska.daily.bible.meditation.data.mappers

import laska.daily.bible.meditation.data.retrofit.courses.AuthorShortDTO
import laska.daily.bible.meditation.data.retrofit.courses.CourseShortDTO
import laska.daily.bible.meditation.domain.courses.Author
import laska.daily.bible.meditation.domain.courses.Course
import javax.inject.Inject

class CoursesMapper @Inject constructor(
    private val languageMapper: LanguageMapper,
    private val homePlacementMapper: HomePlacementMapper
) {

    fun mapAuthorShortDTOToAuthorDataModel(authorShortDTO: AuthorShortDTO): Author{
        return Author(
            id = authorShortDTO.id,
            name = authorShortDTO.name,
            imageURL = authorShortDTO.photo.url,
            description = ""
        )
    }
    fun mapCourseShortDtoToCourseDataModel(courseShortDTO: CourseShortDTO): Course{
        return Course(
            id = courseShortDTO.id,
            title = courseShortDTO.title,
            language = languageMapper.mapStringToLanguage(courseShortDTO.language),
            homePlacement = homePlacementMapper.mapStringToHomePlacement(courseShortDTO.homePlacement),
            shortDescription = courseShortDTO.shortDescription,
            description = "",
            imageURL = courseShortDTO.cover.url,
            authors = courseShortDTO.authors.map { mapAuthorShortDTOToAuthorDataModel(it) },
            episodeCount = courseShortDTO.episodesCount,
            episodeAvailableCount = courseShortDTO.episodesAvailableCount
        )

    }
}