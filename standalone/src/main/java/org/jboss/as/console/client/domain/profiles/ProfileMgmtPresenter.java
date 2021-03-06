package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.shared.SubsystemRecord;
import org.jboss.as.console.client.shared.SubsystemStore;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class ProfileMgmtPresenter
        extends Presenter<ProfileMgmtPresenter.MyView, ProfileMgmtPresenter.MyProxy>
        implements ProfileSelectionEvent.ProfileSelectionListener {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;
    private SubsystemStore subsysStore;
    private ServerGroupStore serverGroupStore;
    private boolean revealDefault = true;

    @ProxyCodeSplit
    @NameToken(NameTokens.ProfileMgmtPresenter)
    public interface MyProxy extends Proxy<ProfileMgmtPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setProfiles(List<ProfileRecord> profileRecords);
        void setSubsystems(List<SubsystemRecord> subsystemRecords);
        void setServerGroups(List<ServerGroupRecord> serverGroupRecords);
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public ProfileMgmtPresenter(
            EventBus eventBus,
            MyView view, MyProxy proxy,
            PlaceManager placeManager, ProfileStore profileStore,
            SubsystemStore subsysStore,
            ServerGroupStore serverGroupStore) {

        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.profileStore = profileStore;
        this.subsysStore = subsysStore;
        this.serverGroupStore = serverGroupStore;
    }


    /**
     * Load default view.
     *
     * @param request
     */
    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);

        // reveal default sub page
        if(revealDefault && NameTokens.ProfileMgmtPresenter.equals(request.getNameToken()))
        {
            placeManager.revealRelativePlace(new PlaceRequest(NameTokens.ProfileOverviewPresenter));
            revealDefault = false;
        }
    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_SetMainContent, this);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getEventBus().addHandler(ProfileSelectionEvent.TYPE, this);

        // TODO: when do these refresh?
        getView().setProfiles(profileStore.loadProfiles());
        getView().setServerGroups(serverGroupStore.loadServerGroups());
    }


    @Override
    protected void onReset() {
        super.onReset();

        Console.MODULES.getHeader().highlight(NameTokens.ProfileMgmtPresenter);

        ProfileHeader header = new ProfileHeader("Configuration Profiles");
        Console.MODULES.getHeader().setContent(header);

    }

    @Override
    public void onProfileSelection(String profileName) {
        getView().setSubsystems(subsysStore.loadSubsystems(profileName));
    }
}
