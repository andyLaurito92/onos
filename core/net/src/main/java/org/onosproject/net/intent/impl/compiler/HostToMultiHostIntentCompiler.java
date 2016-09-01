package org.onosproject.net.intent.impl.compiler;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.Path;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.host.HostService;
import org.onosproject.net.intent.HostToMultiHostIntent;
import org.onosproject.net.intent.Intent;
import org.onosproject.net.intent.PathIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alaurito on 25.08.16.
 */
@Component(immediate = true)
public class HostToMultiHostIntentCompiler
        extends ConnectivityIntentCompiler<HostToMultiHostIntent> {

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Activate
    public void activate() {
        intentManager.registerCompiler(HostToMultiHostIntent.class, this);
    }

    @Deactivate
    public void deactivate() {
        intentManager.unregisterCompiler(HostToMultiHostIntent.class);
    }

    @Override
    public List<Intent> compile(HostToMultiHostIntent intent,
                                List<Intent> instalConnectivityIntentCompilerlable) {
        ArrayList<Intent> multicastIntentPaths = new ArrayList<Intent>();
        for (HostId sourceDestination : intent.destinations()) {
            // TODO: Think a better way of doing this that is more similar to the one implemented in ip with class D.
            Path multicastPath = getPath(intent, intent.source(), sourceDestination);
            multicastIntentPaths.add(createPathIntent(
                    multicastPath,
                    hostService.getHost(intent.source()),
                    hostService.getHost(sourceDestination),
                    intent));
        }

        return multicastIntentPaths;
    }

    // Creates a path intent from the specified path and original connectivity intent.
    private Intent createPathIntent(Path path, Host src, Host dst,
                                    HostToMultiHostIntent intent) {
        TrafficSelector selector = DefaultTrafficSelector.builder(intent.selector())
                .matchEthSrc(src.mac()).matchEthDst(dst.mac()).build();
        return PathIntent.builder()
                .appId(intent.appId())
                .selector(selector)
                .treatment(intent.treatment())
                .path(path)
                .constraints(intent.constraints())
                .priority(intent.priority())
                .build();
    }
}
