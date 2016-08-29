package org.onosproject.codec.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.net.HostId;
import org.onosproject.net.intent.ConnectivityIntent;
import org.onosproject.net.intent.HostToMultiHostIntent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.onlab.util.Tools.nullIsIllegal;

/**
 * Created by alaurito on 25.08.16.
 */
public class HostToMultiHostIntentCodec extends JsonCodec<HostToMultiHostIntent> {

    private static final String SOURCE = "source";
    private static final String DESTINATIONS = "destinations";

    @Override
    public ObjectNode encode(HostToMultiHostIntent intent, CodecContext context) {
        checkNotNull(intent, "Host to multi host intent cannot be null");

        final JsonCodec<ConnectivityIntent> connectivityIntentCodec =
                context.codec(ConnectivityIntent.class);
        final ObjectNode result = connectivityIntentCodec.encode(intent, context);

        final String source = intent.source().toString();
        final String destinations = intent.destinations().toString();
        result.put(SOURCE, source);
        result.put(DESTINATIONS, destinations);

        return result;
    }

    @Override
    public HostToMultiHostIntent decode(ObjectNode json, CodecContext context) {
        HostToMultiHostIntent.Builder builder = HostToMultiHostIntent.builder();

        IntentCodec.intentAttributes(json, context, builder);
        ConnectivityIntentCodec.intentAttributes(json, context, builder);

        String source = nullIsIllegal(json.get(SOURCE),
                SOURCE + IntentCodec.MISSING_MEMBER_MESSAGE).asText();
        builder.source(HostId.hostId(source));


        nullIsIllegal(json.get(DESTINATIONS),
                DESTINATIONS + IntentCodec.MISSING_MEMBER_MESSAGE);
        ArrayNode destinationsJson = (ArrayNode) json.get(DESTINATIONS);
        if (destinationsJson != null) {
            Set<HostId> destinations = new HashSet<HostId>();
            Iterator<JsonNode> destinationsIterator = destinationsJson.iterator();
            while (destinationsIterator.hasNext()) {
                destinations.add(HostId.hostId(destinationsIterator.next().asText()));
            }

            builder.destinations(destinations);
        }

        return builder.build();
    }
}
