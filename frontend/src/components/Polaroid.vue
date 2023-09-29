<script>
import "@fontsource/oleo-script";

export default {
  data() {
    return {
      degree: (Math.random() < 0.5 ? -5 : 5) * Math.random()
    }
  },
  methods: {
    isPortrait() {
      return (this.imageData.smallImage.height / this.imageData.smallImage.width).toFixed(1) >= 1;
    },
    aspectRatio() {
      return this.imageData.smallImage.width / this.imageData.smallImage.height;
    }
  },
  computed: {
    rotate() {
      return {transform: 'rotate(' + this.degree + 'deg)'}
    },
    imageWidth() {
      switch (this.$vuetify.display.name) {
        case 'xs':
          return this.$vuetify.display.width * 0.85
        case 'sm':
          return '400'
        case 'md':
        case 'lg':
        case 'xl':
        case 'xxl':
          return '500'
        default:
          return '280'
      }
    }
  },
  props: ['imageData']

}

</script>

<template>

  <v-card class="polaroid ma-2" :style="rotate" :max-width="parseInt(imageWidth) + 25">
    <v-img
        :width="imageWidth"
        class="ma-3"
        :src="imageData.smallImage.url"
        :lazy-src="isPortrait()? 'lazy-portrait.jpg' : 'lazy-landscape.jpg'"
        :aspect-ratio="aspectRatio()"
        :alt="imageData.name"
        contain
    />
    <v-row>
      <v-col>
        <v-card-text class="handwriting">
          <h1 class="font-weight-thin">{{ imageData.name }}</h1>
          <h5 class="font-weight-thin">{{ imageData.humanReadableDateTime }}</h5>
        </v-card-text>
      </v-col>
    </v-row>
  </v-card>

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
