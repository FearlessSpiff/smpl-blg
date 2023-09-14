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
      return (this.image.height / this.image.width).toFixed(1) >= 1;
    },
    aspectRatio() {
      return this.image.width / this.image.height;
    }
  },
  computed: {
    rotate() {
      return {transform: 'rotate(' + this.degree + 'deg)'}
    },
    imageWidth() {
      switch (this.$vuetify.display.name) {
        case 'xs':
          return '280'
        case 'sm':
          return '400'
        case 'md':
        case 'lg':
        case 'xl':
        case 'xxl':
          return '600'
        default:
          return '280'
      }
    }
  },
  props: ['image']

}

</script>

<template>

  <v-card class="polaroid ma-2" :style="rotate">
    <v-img
        :width="imageWidth"
        class="ma-3"
        :src="'/api/v1/' + image.thumbUrl"
        :lazy-src="isPortrait()? 'lazy-portrait.jpg' : 'lazy-landscape.jpg'"
        :aspect-ratio="aspectRatio()"
        :alt="image.name"
        contain
    />
    <v-card-text class="handwriting">
      <h1 class="font-weight-thin">{{ image.name }}</h1>
      <h5 class="font-weight-thin">{{ image.dateTime }}</h5>
    </v-card-text>
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
