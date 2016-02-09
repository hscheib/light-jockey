package te.light_jockey.domain.hue

import groovy.util.logging.Slf4j
import te.light_jockey.domain.echo_nest.EchoNestSearch
import te.light_jockey.domain.echo_nest.EchoNestSong

import java.math.MathContext
import java.math.RoundingMode

@Slf4j
class LightTransitionState {
    public static final MathContext TO_WHOLE_NUMBER = new MathContext(1, RoundingMode.HALF_UP)
    public static final int DANCEABILITY_DEFAULT = 50
    public static final int ENERGY_DEFAULT = 50
    public static final int TEMPO_DEFAULT = 100

    Double percentChanceToTurnOff
    int secondsBetweenTransitions = 10
    int transitionDuration = 10
    int minBrightness = 100
    int maxBrightness = 100
    int saturation = 100

    void updateState(EchoNestSearch search) {
        Integer danceability = DANCEABILITY_DEFAULT
        Integer energy = ENERGY_DEFAULT
        Integer tempo = TEMPO_DEFAULT

        if (search.hasResults() && search.songs.first().hasMetadata()) {
            EchoNestSong.Metadata metadata = search.songs.first().metadata
            danceability = (metadata.danceability * 100).round(TO_WHOLE_NUMBER).toInteger()
            energy = (metadata.energy * 100).round(TO_WHOLE_NUMBER).toInteger()
            tempo = (metadata.tempo * 100).round(TO_WHOLE_NUMBER).toInteger()
        }

        log.info "Danceability = ${danceability}% | Energy = ${energy}% | Tempo = ${tempo}bpm"

        // Higher tempo = faster transitions that are more frequent
        switch (tempo) {
            case (0..100):
                secondsBetweenTransitions = 10
                transitionDuration = 10
                break
            case (99..119):
                secondsBetweenTransitions = 5
                transitionDuration = 7
                break
            case (120..159):
                secondsBetweenTransitions = 2
                transitionDuration = 3
                break
            default:    // 160 bpm or greater
                secondsBetweenTransitions = 1
                transitionDuration = 0
        }

        // Higher energy = more saturation
        switch (energy) {
            case (0..19):
                saturation = 50
                break
            case (20..39):
                saturation = 100
                break
            case (40..59):
                saturation = 120
                break
            case (60..79):
                saturation = 170
                break
            case (80..99):
                saturation = 200
                break
            default:    // 100% or greater
                saturation = 225
        }

        // Higher danceability = brighter and more likely to switch on & off
        switch (danceability) {
            case (0..19):
                maxBrightness = 60
                minBrightness = 40
                percentChanceToTurnOff = 0
                break
            case (20..39):
                maxBrightness = 100
                minBrightness = 80
                percentChanceToTurnOff = 0
                break
            case (40..59):
                maxBrightness = 150
                minBrightness = 90
                percentChanceToTurnOff = 0.1
                break
            case (60..79):
                maxBrightness = 200
                minBrightness = 100
                percentChanceToTurnOff = 0.2
                break
            case (80..99):
                maxBrightness = 225
                minBrightness = 150
                percentChanceToTurnOff = 0.3
                break
            default:    // 100% or greater
                maxBrightness = 255
                minBrightness = 200
                percentChanceToTurnOff = 0.4
        }

        log.debug("State updated to {}", this.toString())
    }

    String toString(){
        String classStateAsString = "${this.class.simpleName}("
        properties.each { key, value ->
            if(key != 'class') {
                classStateAsString += "\n\t$key = $value"
            }
        }
        return classStateAsString.toString() + "\n)"
    }
}
