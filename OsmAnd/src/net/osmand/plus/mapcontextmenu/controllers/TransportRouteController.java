package net.osmand.plus.mapcontextmenu.controllers;

import android.view.View;
import android.view.View.OnClickListener;

import net.osmand.binary.OsmandOdb.TransportRouteStop;
import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.data.TransportStop;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.mapcontextmenu.MapContextMenu;
import net.osmand.plus.mapcontextmenu.MenuBuilder;
import net.osmand.plus.mapcontextmenu.MenuController;
import net.osmand.plus.mapcontextmenu.controllers.TransportStopController.TransportStopRoute;
import net.osmand.plus.views.TransportStopsLayer;

import java.util.List;

public class TransportRouteController extends MenuController {

	private TransportStopRoute transportRoute;

	public TransportRouteController(final MapActivity mapActivity, PointDescription pointDescription,
									final TransportStopRoute transportRoute) {
		super(new MenuBuilder(mapActivity), pointDescription, mapActivity);
		this.transportRoute = transportRoute;
		builder.setShowOnlinePhotos(false);
		toolbarController = new ContextMenuToolbarController(this);
		toolbarController.setTitle(getNameStr());
		toolbarController.setOnBackButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mapActivity.getContextMenu().backToolbarAction(TransportRouteController.this);
			}
		});
		toolbarController.setOnTitleClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMenuAndRoute(getLatLon(), true);
			}
		});
		toolbarController.setOnCloseButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mapActivity.getContextMenu().closeToolbar(TransportRouteController.this);
			}
		});
	}

	@Override
	protected void setObject(Object object) {
		if (object instanceof TransportStopRoute) {
			this.transportRoute = (TransportStopRoute) object;
		}
	}

	@Override
	protected Object getObject() {
		return transportRoute;
	}

	@Override
	public int getLeftIconId() {
		return this.transportRoute.type == null ?
				R.drawable.mx_public_transport :
				this.transportRoute.type.getTopResourceId();
	}

	@Override
	public boolean needStreetName() {
		return false;
	}

	@Override
	public boolean displayDistanceDirection() {
		return false;
	}

	@Override
	public boolean fabVisible() {
		return false;
	}

	@Override
	public boolean isClosable() {
		return false;
	}

	@Override
	public boolean buttonsVisible() {
		return false;
	}

	@Override
	public String getTypeStr() {
		if (transportRoute.refStop != null) {
			return transportRoute.refStop.getName();
		} else if (transportRoute.stop != null) {
			return transportRoute.stop.getName();
		} else {
			return getPointDescription().getTypeName();
		}
	}

	@Override
	public void onShow() {
		super.onShow();
		showRoute();
	}

	@Override
	public void onClose() {
		super.onClose();
		resetRoute();
	}

	public void onAcquireNewController(PointDescription pointDescription, Object object) {
		if (object instanceof TransportRouteStop) {
			resetRoute();
		}
	}

	@Override
	public void addPlainMenuItems(String typeStr, PointDescription pointDescription, final LatLon latLon) {
		super.addPlainMenuItems(typeStr, pointDescription, latLon);
		List<TransportStop> stops = transportRoute.route.getForwardStops();
		boolean useEnglishNames = getMapActivity().getMyApplication().getSettings().usingEnglishNames();
		int currentStop = -1;
		for (int i = 0; i < stops.size(); i++) {
			final TransportStop stop = stops.get(i);
			if (stop.getName().equals(transportRoute.stop.getName())) {
				currentStop = i;
				break;
			}
		}
		int defaultIcon = transportRoute.type == null ? R.drawable.mx_route_bus_ref : transportRoute.type.getResourceId();
		int startPosition = 0;
		if (!transportRoute.showWholeRoute) {
			startPosition = (currentStop == -1 ? 0 : currentStop);
			if (currentStop > 0) {
				addPlainMenuItem(defaultIcon, getMapActivity().getString(R.string.route_stops_before, currentStop),
						false, false, new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								MapContextMenu menu = getMapActivity().getContextMenu();
								menu.showOrUpdate(latLon, getPointDescription(), transportRoute);
							}
						});
			}
		}
		for (int i = startPosition; i < stops.size(); i++) {
			final TransportStop stop = stops.get(i);
			final String name = useEnglishNames ? stop.getEnName(true) : stop.getName();
			addPlainMenuItem(currentStop == i ? R.drawable.ic_action_marker_dark : defaultIcon,
					name, false, false, new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (mapContextMenu != null) {
								transportRoute.stop = stop;
								transportRoute.refStop = stop;
								PointDescription pd = new PointDescription(PointDescription.POINT_TYPE_TRANSPORT_ROUTE,
										transportRoute.getDescription(getMapActivity().getMyApplication(), false));

								LatLon stopLocation = stop.getLocation();
								if (mapContextMenu.isVisible()) {
									mapContextMenu.updateMapCenter(stopLocation);
								} else {
									mapContextMenu.setMapCenter(stopLocation);
									mapContextMenu.setMapPosition(getMapActivity().getMapView().getMapPosition());
								}
								mapContextMenu.setCenterMarker(true);
								mapContextMenu.setMapZoom(15);
								mapContextMenu.showOrUpdate(stopLocation, pd, transportRoute);
							}
							/*
							PointDescription pd = new PointDescription(PointDescription.POINT_TYPE_TRANSPORT_STOP,
									getMapActivity().getString(R.string.transport_Stop), name);
							LatLon stopLocation = stop.getLocation();
							getMapActivity().getMyApplication().getSettings()
									.setMapLocationToShow(stopLocation.getLatitude(), stopLocation.getLongitude(),
									15, pd, false, transportRoute);
							MapActivity.launchMapActivityMoveToTop(getMapActivity());
							*/
						}
					});
		}
	}

	private void showMenuAndRoute(LatLon latLon, boolean centerMarker) {
		MapContextMenu menu = getMapActivity().getContextMenu();
		if (centerMarker) {
			menu.setCenterMarker(true);
		}
		menu.show(latLon, getPointDescription(), transportRoute);
	}

	private void showRoute() {
		transportRoute.showWholeRoute = true;
		TransportStopsLayer stopsLayer = getMapActivity().getMapLayers().getTransportStopsLayer();
		int cz = transportRoute.calculateZoom(0, getMapActivity().getMapView().getCurrentRotatedTileBox());
		getMapActivity().changeZoom(cz - getMapActivity().getMapView().getZoom());
		stopsLayer.setRoute(transportRoute.route);
	}

	private void resetRoute() {
		TransportStopsLayer stopsLayer = getMapActivity().getMapLayers().getTransportStopsLayer();
		stopsLayer.setRoute(null);
	}
}
