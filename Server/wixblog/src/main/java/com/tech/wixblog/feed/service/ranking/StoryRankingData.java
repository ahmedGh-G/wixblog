import java.util.UUID;

public record StoryRankingData(
        UUID storyId,
        long likes,
        long comments
) {
}
