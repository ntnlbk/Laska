package laska.daily.bible.meditation.data.mappers

import laska.daily.bible.meditation.data.retrofit.courses.AuthorDTO
import laska.daily.bible.meditation.data.retrofit.courses.AuthorShortDTO
import laska.daily.bible.meditation.data.retrofit.courses.CourseDTO
import laska.daily.bible.meditation.data.retrofit.courses.CourseShortDTO
import laska.daily.bible.meditation.data.retrofit.courses.EpisodeShortDTO
import laska.daily.bible.meditation.domain.courses.Author
import laska.daily.bible.meditation.domain.courses.Course
import laska.daily.bible.meditation.domain.courses.Episode
import laska.daily.bible.meditation.domain.courses.EpisodeState
import javax.inject.Inject

class CoursesMapper @Inject constructor(
    private val languageMapper: LanguageMapper,
    private val homePlacementMapper: HomePlacementMapper
) {

    fun mapAuthorShortDTOToAuthorDataModel(authorShortDTO: AuthorShortDTO): Author {
        return Author(
            id = authorShortDTO.id,
            name = authorShortDTO.name,
            imageURL = authorShortDTO.photo.url,
            description = ""
        )
    }

    fun mapAuthorDTOToAuthorDataModel(authorDTO: AuthorDTO): Author {
        return Author(
            id = authorDTO.id,
            name = authorDTO.name,
            imageURL = authorDTO.photo.url,
            description = authorDTO.description
        )
    }

    fun mapCourseShortDtoToCourseDataModel(courseShortDTO: CourseShortDTO): Course {
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

    fun mapShortEpisodeDTOToEpisodeDataModel(episodeShortDTO: EpisodeShortDTO): Episode {
        return Episode(
            id = episodeShortDTO.id,
            courseId = episodeShortDTO.courseId,
            number = episodeShortDTO.number,
            title = episodeShortDTO.title,
            subtitle = episodeShortDTO.subtitle,
            scriptureReference = "",
            imageURL = "",
            authors = listOf(),
            audioURL = "",
            duration = episodeShortDTO.duration,
            text = "",
            state = EpisodeState.LOCKED
        )
    }

    fun mapCourseDTOToCourseDataModel(courseDTO: CourseDTO): Course{
        return Course(
            id = courseDTO.id,
            title = courseDTO.title,
            language = languageMapper.mapStringToLanguage(courseDTO.language),
            homePlacement = homePlacementMapper.mapStringToHomePlacement(courseDTO.homePlacement),
            shortDescription = courseDTO.shortDescription,
            description = courseDTO.description,
            imageURL = courseDTO.cover.url,
            authors = courseDTO.authors.map { mapAuthorDTOToAuthorDataModel(it) },
            episodeCount = courseDTO.episodesCount,
            episodeAvailableCount = courseDTO.episodesAvailableCount
        )
    }
}