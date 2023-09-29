<script>
import "@fontsource/oleo-script";
import '@mdi/font/css/materialdesignicons.css'
import Polaroid from './Polaroid.vue'
import {mdiDownload} from '@mdi/js'

export default {
  components: {Polaroid},
  methods: {
    mdiDownload() {
      return mdiDownload
    },
    isPortrait() {
      return (this.imageData.bigImage.height / this.imageData.bigImage.width).toFixed(1) >= 1;
    },
    aspectRatio() {
      return this.imageData.bigImage.width / this.imageData.bigImage.height;
    }
  },
  data() {
    return {
      dialog: false
    }
  },
  computed: {
    imageWidth() {
      if (this.isPortrait()) {
        const aspect = this.imageData.bigImage.height / this.imageData.bigImage.width;
        const maxHeight = this.$vuetify.display.height * 0.8;

        return maxHeight / aspect;
      } else {
        return this.$vuetify.display.width * 0.8;
      }
    }
  },
  props: ['imageData']

}

</script>

<template>

  <v-dialog v-model="dialog" :width="imageWidth + 40" height="auto">

    <template v-slot:activator="{ props }">
      <polaroid class="ma-2" :imageData="imageData" v-bind="props"/>
    </template>

    <div class="polaroid ma-2">
      <v-img
          class="ma-3"
          :src="imageData.bigImage.url"
          :lazy-src="isPortrait()? 'lazy-portrait.jpg' : 'lazy-landscape.jpg'"
          :alt="imageData.name"
          :width="imageWidth"
          :aspect-ratio="aspectRatio()"
          contain
      />
      <div class="flex-hack">
        <v-tooltip text="Download Original">
          <template v-slot:activator="{ props }">
            <v-btn
                v-bind="props"
                class="download-button"
                density="comfortable"
                variant="text"
                :icon="mdiDownload()"
                color="gray"
                :href="imageData.originalImage.url"
                download
            ></v-btn>
          </template>
        </v-tooltip>
        <div class="text">
          <div class="handwriting ma-3">
            <h1 class="font-weight-thin">{{ imageData.name }}</h1>
            <h5 class="font-weight-thin">{{ imageData.humanReadableDateTime }}</h5>
          </div>
        </div>
      </div>
    </div>

  </v-dialog>
</template>


<style scoped>

.polaroid {
  background-color: white;
  color: #121212;
  box-shadow: 0 3px 3px rgba(0, 0, 0, 0.2);
  font-family: "Oleo Script", serif;
}

.handwriting {
  text-align: right !important;
  display: block;
}

.flex-hack {
  display: flex;
}

.text {
  margin-left: auto;
  margin-right: 0;
}

.download-button {
  align-self: flex-end;
}

</style>
