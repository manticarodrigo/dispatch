export const createHTMLMapMarker = ({ lib, ...args }) => {
  const OverlayView = lib.maps.OverlayView;

  class HTMLMapMarker extends OverlayView {
    constructor() {
      super();
      this.latlng = args.latlng;
      this.html = args.html;
      this.setMap(args.map);
    }

    createDiv() {
      this.div = document.createElement("div");
      this.div.style.position = "absolute";
      if (this.html) {
        this.div.innerHTML = this.html;
      }
      lib.maps.event.addDomListener(this.div, "click", (event) => {
        lib.maps.event.trigger(this, "click");
      });
    }

    appendDivToOverlay() {
      const panes = this.getPanes();
      panes.overlayImage.appendChild(this.div);
    }

    positionDiv() {
      const point = this.getProjection().fromLatLngToDivPixel(this.latlng);
      let offset = 0;

      if (point) {
        this.div.style.left = `${point.x - offset}px`;
        this.div.style.top = `${point.y - offset}px`;
      }
    }

    draw() {
      if (this.latlng) {
        if (!this.div) {
          this.createDiv();
          this.appendDivToOverlay();
        }
        this.positionDiv();
      }
    }

    update(latlng) {
      this.latlng = latlng;
      this.draw();
    }

    remove() {
      if (this.div) {
        this.div.parentNode.removeChild(this.div);
        this.div = null;
      }
    }

    getPosition() {
      return this.latlng;
    }

    getDraggable() {
      return false;
    }
  }

  return new HTMLMapMarker();
};
