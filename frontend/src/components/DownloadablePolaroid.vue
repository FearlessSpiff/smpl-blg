<script>
import "@fontsource/oleo-script";
import Polaroid from './Polaroid.vue'

export default {
  components: {Polaroid},
  methods: {
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

  <v-dialog v-model="dialog" width="auto" height="auto">

    <template v-slot:activator="{ props }">
      <polaroid class="ma-2" :imageData="imageData" v-bind="props"/>
    </template>

    <v-card class="polaroid ma-2">
      <v-img
          class="ma-3"
          :src="imageData.bigImage.url"
          :lazy-src="isPortrait()? 'lazy-portrait.jpg' : 'lazy-landscape.jpg'"
          :alt="imageData.name"
          :width="imageWidth"
          :aspect-ratio="aspectRatio()"
          contain
      />
      <v-card-text class="handwriting">
        <h1 class="font-weight-thin">{{ imageData.name }}</h1>
        <h5 class="font-weight-thin">{{ imageData.humanReadableDateTime }}</h5>
      </v-card-text>
    </v-card>

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
}

</style>
