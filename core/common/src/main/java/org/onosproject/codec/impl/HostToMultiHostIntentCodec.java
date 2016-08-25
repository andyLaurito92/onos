package org.onosproject.codec.impl;

import com.eclipsesource.json.JsonArray;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.net.HostId;
import org.onosproject.net.intent.ConnectivityIntent;
import org.onosproject.net.intent.HostToMultiHostIntent;

import java.util.HashSet;
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


        ObjectNode destinations_json = nullIsIllegal(get(json, DESTINATIONS),
                DESTINATIONS + IntentCodec.MISSING_MEMBER_MESSAGE);
        if (destinations_json != null) {
            Set<HostId> destinations = new HashSet<HostId>();
            while( destinations_json.iterator().hasNext() )
                destinations.add(HostId.hostId(destinations_json.iterator().next().toString()));

            builder.destinations(destinations);
        }

        return builder.build();
    }
}
