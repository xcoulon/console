package org.jboss.as.console.client.server.deployments;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.shared.DeploymentRecord;
import org.jboss.as.console.client.shared.DeploymentStore;

import java.util.List;

/**
 * Manages deployments on a standalone server.
 * Acts as a presenter component.
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
public class DeploymentToolPresenter extends Presenter<DeploymentToolPresenter.DeploymentToolView,
        DeploymentToolPresenter.DeploymentToolProxy> {

    private PlaceManager placeManager;
    private DeploymentStore store;

    public interface DeploymentToolView extends SuspendableView {
        void setPresenter(DeploymentToolPresenter presenter);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.deploymentTool)
    public interface DeploymentToolProxy extends ProxyPlace<DeploymentToolPresenter> {}

    @Inject
    public DeploymentToolPresenter(
            EventBus eventBus, DeploymentToolView view,
            DeploymentToolProxy proxy, PlaceManager placeManager,
            DeploymentStore store) {

        super(eventBus, view, proxy);

        this.store = store;
        this.placeManager = placeManager;
    }

    public List<DeploymentRecord> getRecords() {
        return store.loadDeployments();
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerMgmtApplicationPresenter.TYPE_MainContent, this);
    }
}
