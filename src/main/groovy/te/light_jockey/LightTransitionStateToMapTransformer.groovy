package te.light_jockey

import groovy.util.logging.Slf4j
import te.light_jockey.domain.hue.LightTransitionState

@Slf4j
class LightTransitionStateToMapTransformer {

    Map transform(LightTransitionState transitionState) {
        int randomIntBetween1and10 = randomIntBetween(1, 10)
        int thresholdValueForTurningOff = (transitionState.percentChanceToTurnOff * 10).round()
        boolean shouldRandomlyTurnOff = (randomIntBetween1and10 >= thresholdValueForTurningOff)

        Map payload = shouldRandomlyTurnOff ? [on: false] : [
                on            : true,
                hue           : randomIntBetween(0, 65000),
                bri           : randomIntBetween(transitionState.minBrightness, transitionState.maxBrightness),
                sat           : transitionState.saturation,
                transitionTime: transitionState.transitionDuration
        ]

        log.debug("Transition payload:")
        payload.each { key, value ->
            log.debug("{} = {}", key, value)
        }

        return payload
    }

    private static int randomIntBetween(int lowerBound, int upperBound) {
        // Since "nextInt(upperBound - lowerBound) + lowerBound" is inclusive on lowerBound
        // and exclusive on upperBound, we add one to upperBound to make it inclusive.
        int realUpperBound = upperBound + 1;
        return new Random().nextInt(realUpperBound - lowerBound) + lowerBound;
    }
}
