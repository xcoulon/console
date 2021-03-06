package org.jboss.as.console.client.domain.model;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public interface HostInformationStore {
    List<Host> getHosts();
    List<Server> getServerConfigurations(String name);
    List<ServerInstance> getServerInstances(String host);
}
