package te.light_jockey.rest_services

import groovy.util.logging.Slf4j
import te.light_jockey.LightTransitionStateToMapTransformer
import te.light_jockey.domain.echo_nest.EchoNestSearch
import te.light_jockey.domain.hue.LightTransitionState
import wslite.rest.ContentType
import wslite.rest.RESTClient

import java.math.MathContext
import java.math.RoundingMode

@Slf4j
class PhillipsHueService {
    public static final Map TO_BRIGHT_WHITE = [on: true, sat: 50, bri: 200, hue: 10_000, transitionTime: 50]

    final LightTransitionStateToMapTransformer lightTransitionStateToMapTransformer
    final RESTClient hueApiEndpoint

    PhillipsHueService(String hueBridgeUrl) {
        this.lightTransitionStateToMapTransformer = new LightTransitionStateToMapTransformer()
        this.hueApiEndpoint = new RESTClient(hueBridgeUrl)
    }

    void triggerLightTransition(String lightId, Map payload) {
        hueApiEndpoint.put(path: "/lights/$lightId/state") {
            type ContentType.JSON
            charset "UTF-8"
            json payload
        }
    }

    void triggerLightTransition(String lightId, LightTransitionState lightTransitionState) {
        Map payload = lightTransitionStateToMapTransformer.transform(lightTransitionState)
        hueApiEndpoint.put(path: "/lights/$lightId/state") {
            type ContentType.JSON
            charset "UTF-8"
            json payload
        }
    }
}
